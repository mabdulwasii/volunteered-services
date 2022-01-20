package org.volunteered.apps.recommendation.util

import org.springframework.data.domain.Page
import org.volunteered.apps.recommendation.entity.RecommendationEntity
import org.volunteered.apps.recommendation.entity.RecommendationRequestEntity
import org.volunteered.libs.proto.common.v1.PaginationRequest
import org.volunteered.libs.proto.common.v1.paginationResponse
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationRequestsResponse
import org.volunteered.libs.proto.recommendation.v1.Recommendation
import org.volunteered.libs.proto.recommendation.v1.RecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.RequestRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.RequestStatus
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
        ):
            MutableList<Recommendation> {
                val recommendationDtoList = mutableListOf<Recommendation>()
                recommendationEntityList.forEach {
                    val recommendationDto = transformRecommendationEntityToRecommendationDto(it)
                    recommendationDtoList.add(recommendationDto)
                }
                return recommendationDtoList
            }

        private fun transformRecommendationEntityToRecommendationDto(recommendationEntity: RecommendationEntity) = recommendation {
            id = recommendationEntity.id!!
            positionHeld = recommendationEntity.positionHeld
            duration = recommendationEntity.duration
            recommenderPosition = recommendationEntity.recommenderPosition
            body = recommendationEntity.body
            userIds.addAll(recommendationEntity.userIds)
            organizationSubsidiaryId = recommendationEntity.organizationSubsidiaryId
        }
    }
}
