package org.volunteered.apps.recommendation.util

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.volunteered.apps.recommendation.entity.RecommendationEntity
import org.volunteered.apps.recommendation.entity.RecommendationRequestEntity
import org.volunteered.libs.proto.common.v1.Gender
import org.volunteered.libs.proto.common.v1.PaginationRequest
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.common.v1.paginationResponse
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationRequestsResponse
import org.volunteered.libs.proto.recommendation.v1.Recommendation
import org.volunteered.libs.proto.recommendation.v1.RecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.RequestRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.RequestStatus
import org.volunteered.libs.proto.recommendation.v1.WriteRecommendation
import org.volunteered.libs.proto.recommendation.v1.getOrganizationRecommendationRequestsResponse
import org.volunteered.libs.proto.recommendation.v1.recommendation
import org.volunteered.libs.proto.recommendation.v1.recommendationRequest

class DtoTransformer {
    companion object {
        fun transformRequestRecommendationRequestToRecommendationRequestEntity(
            request: RequestRecommendationRequest
        ): RecommendationRequestEntity {
            return RecommendationRequestEntity(
                userId = request.userId,
                organizationSubsidiaryId = request.organizationSubsidiaryId,
                status = RequestStatus.AWAITING_RESPONSE
            )
        }

        fun transformRecommendationRequestEntityToRecommendationRequestDto(
            recommendationRequestEntity:
                RecommendationRequestEntity
        ) = recommendationRequest {
            id = recommendationRequestEntity.id!!
            userId = recommendationRequestEntity.userId
            organizationSubsidiaryId = recommendationRequestEntity.organizationSubsidiaryId
            status = recommendationRequestEntity.status
        }

        fun transformOrganizationRecommendationRequestListToGetOrganizationRecommendationRequestsResponse(
            organizationRecommendationRequestList: Page<RecommendationRequestEntity>,
            paginationRequest: PaginationRequest
        ): GetOrganizationRecommendationRequestsResponse {
            return getOrganizationRecommendationRequestsResponse {
                val recommendationRequestDtoList = mutableListOf<RecommendationRequest>()

                organizationRecommendationRequestList.content.forEach {
                    val recommendationRequestDto = transformRecommendationRequestEntityToRecommendationRequestDto(it)
                    recommendationRequestDtoList.add(recommendationRequestDto)
                }

                val paginationResponse = paginationResponse {
                    total = organizationRecommendationRequestList.totalElements
                    limitPerPage = paginationRequest.limitPerPage
                    page = paginationRequest.page
                }
                recommendationRequests.addAll(recommendationRequestDtoList)
                pagination = paginationResponse
            }
        }

        fun transformRecommendationEntityListToRecommendationDtoList(
            recommendationEntityList: Page<RecommendationEntity>
        ): MutableList<Recommendation> {
            val recommendationDtoList = mutableListOf<Recommendation>()
            recommendationEntityList.forEach {
                val recommendationDto = transformRecommendationEntityToRecommendationDto(it)
                recommendationDtoList.add(recommendationDto)
            }
            return recommendationDtoList
        }

        fun transformRecommendationEntityToRecommendationDto(recommendationEntity: RecommendationEntity) = recommendation {
            id = recommendationEntity.id!!
            positionHeld = recommendationEntity.positionHeld
            duration = recommendationEntity.duration
            recommenderPositionId = recommendationEntity.recommenderPositionId
            body = recommendationEntity.body
            userId = recommendationEntity.userId
            organizationSubsidiaryId = recommendationEntity.organizationSubsidiaryId
        }

        fun buildPageable(pagination: PaginationRequest): PageRequest =
            PageRequest.of(
                pagination.page,
                pagination.limitPerPage
            )

        fun buildPaginationResponse(
            totalElements: Long,
            pagination: PaginationRequest
        ) = paginationResponse {
            total = totalElements
            limitPerPage = pagination.limitPerPage
            page = pagination.page
        }

        fun transformWriteRecommendationRequestDtoRecommendationEntity(
            it: WriteRecommendation,
            userId: Long
        ) = RecommendationEntity(
            organizationSubsidiaryId = it.organizationSubsidiaryId,
            positionHeld = it.positionHeld,
            duration = it.duration,
            recommenderPositionId = it.recommenderPositionId,
            body = it.body,
            userId = userId
        )

        fun resolveRecommendationBodyForUser(body: String, user: User) {
            body.replace("{{name}}", user.firstName)
            body.replace("{{email}}", user.email)
            val subject: String
            val objectPronoun: String
            when {
                user.gender.equals(Gender.MALE) -> {
                    subject = "he"
                    objectPronoun = "his"
                }
                else -> {
                    subject = "she"
                    objectPronoun = "her"
                }
            }
            body.replace("{{subject}}", subject)
            body.replace("{{object}}", objectPronoun)
        }
    }
}
