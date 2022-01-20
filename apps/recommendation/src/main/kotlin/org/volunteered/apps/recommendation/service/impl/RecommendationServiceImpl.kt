package org.volunteered.apps.recommendation.service.impl

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.volunteered.apps.recommendation.exception.CreatorDoesNotExistException
import org.volunteered.apps.recommendation.repository.RecommendationRepository
import org.volunteered.apps.recommendation.repository.RecommendationRequestRepository
import org.volunteered.apps.recommendation.service.RecommendationService
import org.volunteered.apps.recommendation.util.DtoTransformer
import org.volunteered.libs.proto.common.v1.id
import org.volunteered.libs.proto.organization.v1.OrganizationServiceGrpcKt
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationRequestsResponse
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.GetRecommendationsResponse
import org.volunteered.libs.proto.recommendation.v1.GetUserRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.OrganizationRecommendationRequests
import org.volunteered.libs.proto.recommendation.v1.Recommendation
import org.volunteered.libs.proto.recommendation.v1.RecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.RequestRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.WriteRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.getRecommendationsResponse
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class RecommendationServiceImpl(
    private val userServiceStub: UserServiceGrpcKt.UserServiceCoroutineStub,
    private val organizationServiceCoroutineStub: OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub,
    private val recommendationRepository: RecommendationRepository,
    private val recommendationRequestRepository: RecommendationRequestRepository
) : RecommendationService {
    override suspend fun requestRecommendation(request: RequestRecommendationRequest): RecommendationRequest {
        val userId = request.userId
        val organizationSubsidiaryId = request.organizationSubsidiaryId
        ensureUserExists(userId)
        ensureOrganizationSubsidiaryExists(organizationSubsidiaryId)

        val recommendationRequestEntity = DtoTransformer
            .transformRequestRecommendationRequestToRecommendationRequestEntity(request)

        val savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequestEntity)

        return DtoTransformer.transformRecommendationRequestEntityToRecommendationRequestDto(savedRecommendationRequest)
    }

    override suspend fun getOrganizationRecommendationRequests(request: OrganizationRecommendationRequests): GetOrganizationRecommendationRequestsResponse {
        val organizationSubsidiary = request.organizationSubsidiaryId
        val pagination = request.pagination
        val paginationLimitPerPage = pagination.limitPerPage
        val paginationPage = pagination.page
        val pageable = PageRequest.of(paginationPage, paginationLimitPerPage)

        val organizationRecommendationRequestPage =
            recommendationRequestRepository.findAllByOrganizationSubsidiaryId(organizationSubsidiary, pageable)

        return DtoTransformer.transformOrganizationRecommendationRequestListToGetOrganizationRecommendationRequestsResponse(organizationRecommendationRequestPage, pagination)
    }

    override suspend fun writeRecommendation(request: WriteRecommendationRequest): Recommendation {
        TODO("Not yet implemented")
    }

    override suspend fun getOrganizationRecommendations(request: GetOrganizationRecommendationsRequest): GetRecommendationsResponse {
        return getRecommendationsResponse {
            val pageable = DtoTransformer.buildPageable(request.pagination)
            val retrievedRecommendations =
                recommendationRepository.findAllByOrganizationSubsidiaryId(request.organizationSubsidiaryId, pageable)
            recommendations.addAll(
                DtoTransformer.transformRecommendationEntityListToRecommendationDtoList(retrievedRecommendations)
            )
            pagination = DtoTransformer.buildPaginationResponse(
                retrievedRecommendations.totalElements,
                request.pagination
            )
        }
    }

    override suspend fun getUserRecommendations(request: GetUserRecommendationsRequest): GetRecommendationsResponse {
        TODO("Not yet implemented")
    }

    private suspend fun ensureUserExists(userId: Long) {
        val creatorExists = userServiceStub.existsById(
            id { id = userId }
        ).value

        if (!creatorExists)
            throw CreatorDoesNotExistException("Creator does not exist")
    }

    private suspend fun ensureOrganizationSubsidiaryExists(organizationSubsidiaryId: Long) {
        val organizationSubsidiaryExists = organizationServiceCoroutineStub.organizationSubsidiaryExistsById(
            id { id = organizationSubsidiaryId }
        ).value

        if (!organizationSubsidiaryExists)
            throw CreatorDoesNotExistException("Creator does not exist")
    }
}
