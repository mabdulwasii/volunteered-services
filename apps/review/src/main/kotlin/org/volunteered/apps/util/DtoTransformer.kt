package org.volunteered.apps.util

import org.springframework.data.domain.Page
import org.volunteered.apps.entity.RatingConfigEntity
import org.volunteered.apps.entity.RatingEntity
import org.volunteered.apps.entity.ReplyReviewEntity
import org.volunteered.apps.entity.ReviewEntity
import org.volunteered.libs.core.extension.whenGreaterThanZero
import org.volunteered.libs.core.extension.whenNotEmpty
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.common.v1.PaginationRequest
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.common.v1.paginationResponse
import org.volunteered.libs.proto.review.v1.CreateRatingConfigRequest
import org.volunteered.libs.proto.review.v1.GetReviewsResponse
import org.volunteered.libs.proto.review.v1.ReplyReviewRequest
import org.volunteered.libs.proto.review.v1.Review
import org.volunteered.libs.proto.review.v1.UpdateRatingConfigRequest
import org.volunteered.libs.proto.review.v1.UpdateReviewRequest
import org.volunteered.libs.proto.review.v1.WriteReviewRequest
import org.volunteered.libs.proto.review.v1.getReviewsResponse
import org.volunteered.libs.proto.review.v1.rating
import org.volunteered.libs.proto.review.v1.ratingConfig
import org.volunteered.libs.proto.review.v1.review
import org.volunteered.libs.proto.review.v1.reviewReply

class DtoTransformer {
    companion object {
        fun transformWriteReviewRequestToReviewEntity(
            request: WriteReviewRequest,
            user: User,
            organizationSubsidiary: OrganizationSubsidiary
        ) : ReviewEntity {

            val reviewEntity = ReviewEntity(
                rating = request.rating,
                body = request.body,
                organizationSubsidiaryCity = organizationSubsidiary.city,
                organizationSubsidiaryId = organizationSubsidiary.id,
                organizationId = organizationSubsidiary.organizationId,
                helpfulCount = 0,
                userId = request.userId.toString(),
                userDisplayName = user.firstName,
                userAvatar = user.profilePhoto
            )
            if (request.anonymous) {
                reviewEntity.userId = StringEncoder.hashValue(user.id.toString())
                user.firstName.whenNotEmpty { reviewEntity.userDisplayName = StringEncoder.hashValue(it) }
                user.profilePhoto.whenNotEmpty { reviewEntity.userAvatar = StringEncoder.hashValue(it) }
            }

            return reviewEntity
        }

        fun transformReviewEntityToReviewDto(reviewEntity: ReviewEntity) = review {
            id = reviewEntity.id!!
            organizationSubsidiaryCity = reviewEntity.organizationSubsidiaryCity
            rating = reviewEntity.rating
            reviewEntity.body?.let { body = it }
            reviewEntity.userDisplayName?.let { userDisplayName = it }
            reviewEntity.userAvatar?.let { userAvatar = it }
            reviewEntity.helpfulCount?.let { helpfulCount = it }
            verified = reviewEntity.verified
        }

        fun transformReviewEntityListToReviewDtoList(reviewEntityList: Page<ReviewEntity>, paginationRequest:
        PaginationRequest): GetReviewsResponse {
            val reviewDtoList = mutableListOf<Review>()

            reviewEntityList.content.forEach {
                val reviewDto = transformReviewEntityToReviewDto(it)
                reviewDtoList.add(reviewDto)
            }

            val paginationResponse = paginationResponse {
                total = reviewEntityList.totalElements
                limitPerPage = paginationRequest.limitPerPage
                page = paginationRequest.page
            }
            return getReviewsResponse {
                reviews.addAll(reviewDtoList)
                pagination = paginationResponse
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

        fun buildReviewEntityFromReviewDto(reviewEntity: ReviewEntity, request: UpdateReviewRequest) {
            request.body.whenNotEmpty { reviewEntity.body = it }
            request.rating.whenGreaterThanZero { reviewEntity.rating = it }
        }

        fun transformCreateRatingConfigRequestToRatingConfigEntity(request: CreateRatingConfigRequest) : RatingConfigEntity {
            return RatingConfigEntity(
                ratingType = request.ratingType,
                weight = request.weight
            )
        }

        fun transformReviewConfigEntityToReviewConfigDto(ratingConfigEntity: RatingConfigEntity) = ratingConfig {
            id = ratingConfigEntity.id!!
            ratingType = ratingConfigEntity.ratingType
            weight = ratingConfigEntity.weight
        }

        fun transformRatingConfigEntityToRatingConfigDto(ratingConfigEntity: RatingConfigEntity) = ratingConfig {
            id = ratingConfigEntity.id!!
            ratingType = ratingConfigEntity.ratingType
            weight = ratingConfigEntity.weight
        }

        fun transformRatingConfigDtoToRatingConfigEntity(
            request: UpdateRatingConfigRequest,
            ratingConfigEntity: RatingConfigEntity
        ) {
            ratingConfigEntity.weight = request.weight
        }

        fun transformRatingEntityToRatingDto(ratingEntity: RatingEntity) = rating {
            id = ratingEntity.id!!
            organizationSubsidiaryId = ratingEntity.organizationSubsidiaryId
            rating = ratingEntity.rating
            unverifiedRatingCount = ratingEntity.unverifiedRatingCount
            verifiedRatingCount = ratingEntity.verifiedRatingCount
        }

    }

}