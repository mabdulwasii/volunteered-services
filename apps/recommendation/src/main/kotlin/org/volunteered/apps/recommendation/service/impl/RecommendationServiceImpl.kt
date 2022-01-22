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
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationRequests
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationRequestsResponse
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.GetUserRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.RecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.RequestRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.WriteRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.WriteRecommendationResponse
import org.volunteered.libs.proto.recommendation.v1.getRecommendationsResponse
import org.volunteered.libs.proto.recommendation.v1.writeRecommendationResponse
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt
import org.volunteered.libs.proto.user.v1.getUserByIdRequest

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class RecommendationServiceImpl(
    private val userServiceStub: UserServiceGrpcKt.UserServiceCoroutineStub,
    private val organizationServiceCoroutineStub: OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub,
    private val recommendationRepository: RecommendationRepository,
    private val recommendationRequestRepository: RecommendationRequestRepository
) : RecommendationService {
    override suspend fun requestRecommendation(request: RequestRecommendationRequest): RecommendationRequest {
        ensureUserExists(request.userId)
        ensureOrganizationSubsidiaryExists(request.organizationSubsidiaryId)

        val recommendationRequestEntity = DtoTransformer
            .transformRequestRecommendationRequestToRecommendationRequestEntity(request)

        val savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequestEntity)

        return DtoTransformer.transformRecommendationRequestEntityToRecommendationRequestDto(savedRecommendationRequest)
    }

    override suspend fun getRecommendationRequestsForOrganization(request: GetOrganizationRecommendationRequests):
            GetOrganizationRecommendationRequestsResponse {
        val organizationSubsidiary = request.organizationSubsidiaryId
        val pagination = request.pagination
        val paginationLimitPerPage = pagination.limitPerPage
        val paginationPage = pagination.page
        val pageable = PageRequest.of(paginationPage, paginationLimitPerPage)

        val organizationRecommendationRequestPage =
            recommendationRequestRepository.findAllByOrganizationSubsidiaryId(organizationSubsidiary, pageable)

        return DtoTransformer.transformOrganizationRecommendationRequestListToGetOrganizationRecommendationRequestsResponse(
            organizationRecommendationRequestPage,
            pagination
        )
    }

    override suspend fun writeRecommendation(request: WriteRecommendationRequest): WriteRecommendationResponse {
        return writeRecommendationResponse {
            request.recommendationsList.forEach {
                it.userIdsList.forEach { userId ->
                    val retrievedUser = userServiceStub.getUserById(
                        getUserByIdRequest {
                            id = userId
                        }
                    )
                    val userRecommendationExists = recommendationRepository.existsByUserIdAndOrganizationSubsidiaryId(
                            userId,
                            it.organizationSubsidiaryId
                        )
                    if (!userRecommendationExists) {
                        DtoTransformer.resolveRecommendationBodyForUser(it.body, retrievedUser)
                        val recommendationEntity = DtoTransformer.transformWriteRecommendationRequestDtoRecommendationEntity(
                            it,
                            userId
                        )
                        recommendations.add(
                            DtoTransformer.transformRecommendationEntityToRecommendationDto(
                                recommendationRepository.save(recommendationEntity)
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun getOrganizationRecommendations(request: GetOrganizationRecommendationsRequest) = getRecommendationsResponse {
        val pageable = DtoTransformer.buildPageable(request.pagination)
        val retrievedRecommendations = recommendationRepository.findAllByOrganizationSubsidiaryId(
            request.organizationSubsidiaryId,
            pageable
        )
        recommendations.addAll(
            DtoTransformer.transformRecommendationEntityListToRecommendationDtoList(retrievedRecommendations)
        )
        pagination = DtoTransformer.buildPaginationResponse(
            retrievedRecommendations.totalElements,
            request.pagination
        )
    }

    override suspend fun getUserRecommendations(request: GetUserRecommendationsRequest) = getRecommendationsResponse {
        val pageable = DtoTransformer.buildPageable(request.pagination)
        val retrievedRecommendations = recommendationRepository.findAllByUserId(
            request.userId,
            pageable
        )
        recommendations.addAll(
            DtoTransformer.transformRecommendationEntityListToRecommendationDtoList(retrievedRecommendations)
        )
        pagination = DtoTransformer.buildPaginationResponse(
            retrievedRecommendations.totalElements,
            request.pagination
        )
    }

    private suspend fun ensureUserExists(userId: Long) {
        val userExists = userServiceStub.existsById(
            id { id = userId }
        ).value

        if (!userExists)
            throw CreatorDoesNotExistException("Creator does not exist")
    }

    private suspend fun ensureOrganizationSubsidiaryExists(organizationSubsidiaryId: Long) {
        val organizationSubsidiaryExists = organizationServiceCoroutineStub.organizationSubsidiaryExistsById(
            id { id = organizationSubsidiaryId }
        ).value

        if (!organizationSubsidiaryExists)
            throw CreatorDoesNotExistException("Organization does not exist")
    }
}
