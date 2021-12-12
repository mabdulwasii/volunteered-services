package org.volunteered.apps.util

import org.springframework.data.domain.Page
import org.volunteered.apps.entity.ReplyReviewEntity
import org.volunteered.apps.entity.ReviewEntity
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.common.v1.PaginationRequest
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.common.v1.paginationResponse
import org.volunteered.libs.proto.review.v1.GetReviewsResponse
import org.volunteered.libs.proto.review.v1.ReplyReviewRequest
import org.volunteered.libs.proto.review.v1.Review
import org.volunteered.libs.proto.review.v1.WriteReviewRequest
import org.volunteered.libs.proto.review.v1.getReviewsResponse
import org.volunteered.libs.proto.review.v1.review
import org.volunteered.libs.proto.review.v1.reviewReply

class DtoTransformer {
    companion object {
        fun transformWriteReviewRequestToReviewEntity(
            request: WriteReviewRequest,
            user: User,
            organizationSubsidiary: OrganizationSubsidiary
        ) : ReviewEntity {
            return ReviewEntity(
                userDisplayName = user.firstName,
                userAvatar = user.profilePhoto,
                rating = request.rating,
                body = request.body,
                organizationSubsidiaryCity = organizationSubsidiary.city,
                userId = user.id,
                organizationSubsidiaryId = organizationSubsidiary.id,
                organizationId = organizationSubsidiary.organizationId,
                helpfulCount = 0
            )
        }

        fun transformReviewEntityToReviewDto(reviewEntity: ReviewEntity) = review {
            id = reviewEntity.id!!
            userDisplayName = reviewEntity.userDisplayName
            organizationSubsidiaryCity = reviewEntity.organizationSubsidiaryCity
            rating = reviewEntity.rating
            body = reviewEntity.body
            reviewEntity.userAvatar?.let { userAvatar = it }
            reviewEntity.helpfulCount?.let { helpfulCount = it }
            reviewEntity.verified?.let { verified = it }
        }

        fun transformReviewEntityListToReviewDtoList(reviewEntityList: Page<ReviewEntity>, paginationRequest:
        PaginationRequest): GetReviewsResponse {
            val reviewDtoList = mutableListOf<Review>()

            reviewEntityList.forEach {
                val reviewDto = transformReviewEntityToReviewDto(it)
                reviewDtoList.add(reviewDto)
            }

            val paginationResponse = paginationResponse {
                total = reviewEntityList.totalElements
                limitPerPage = paginationRequest.limitPerPage
                page = paginationRequest.page
            }
            return getReviewsResponse {
                this.reviews.addAll(reviewDtoList)
                this.pagination = paginationResponse
            }
        }

        fun transformReplyReviewRequestToReplyReviewEntity(request: ReplyReviewRequest, user: User, reviewEntity: ReviewEntity):
                ReplyReviewEntity {
            return ReplyReviewEntity(
                review = reviewEntity,
                userId = user.id,
                userDisplayName = user.firstName,
                userAvatar = user.profilePhoto,
                body = request.body
            )
        }

        fun transformReplyReviewEntityToReplyReviewDto(replyReviewEntity: ReplyReviewEntity) = reviewReply {
            id = replyReviewEntity.id!!
            reviewId = replyReviewEntity.review.id!!
            userDisplayName = replyReviewEntity.userDisplayName
            replyReviewEntity.userAvatar?.let { userAvatar = it }
            body = replyReviewEntity.body
        }
    }
}