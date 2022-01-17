package org.volunteered.apps.review.util

import org.volunteered.apps.entity.RatingConfigEntity
import org.volunteered.apps.entity.RatingEntity
import org.volunteered.apps.entity.ReplyReviewEntity
import org.volunteered.apps.entity.ReviewEntity
import org.springframework.data.domain.Page
import org.volunteered.apps.review.entity.RatingConfigEntity
import org.volunteered.apps.review.entity.RatingEntity
import org.volunteered.apps.review.entity.ReplyReviewEntity
import org.volunteered.apps.review.entity.ReviewEntity
import org.volunteered.libs.core.extension.whenGreaterThanZero
import org.volunteered.libs.core.extension.whenNotEmpty
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.review.v1.CreateRatingConfigRequest
import org.volunteered.libs.proto.review.v1.ReplyReviewRequest
import org.volunteered.libs.proto.review.v1.UpdateRatingConfigRequest
import org.volunteered.libs.proto.review.v1.UpdateReviewRequest
import org.volunteered.libs.proto.review.v1.WriteReviewRequest
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
        ): ReviewEntity {

            val reviewEntity = ReviewEntity(
                rating = request.rating,
                body = request.body,
                organizationSubsidiaryId = organizationSubsidiary.id,
                helpfulCount = 0,
                userId = request.userId.toString(),
            )
            if (request.anonymous) {
                reviewEntity.userId = StringEncoder.hashValue(user.id.toString())
            }

            return reviewEntity
        }

        fun transformReviewEntityToReviewDto(reviewEntity: ReviewEntity, user: User?) = review {
            id = reviewEntity.id!!
            rating = reviewEntity.rating
            reviewEntity.body?.let { body = it }
            reviewEntity.helpfulCount?.let { helpfulCount = it }
            verified = reviewEntity.verified
            user?.profilePhoto?.let { userAvatar = it }
            user?.firstName?.let { userDisplayName = it }
        }

        fun transformReplyReviewRequestToReplyReviewEntity(
            request: ReplyReviewRequest,
            user: User,
            reviewEntity: ReviewEntity
        ): ReplyReviewEntity {
            return ReplyReviewEntity(
                review = reviewEntity,
                userId = user.id,
                body = request.body
            )
        }

        fun transformReplyReviewEntityToReplyReviewDto(replyReviewEntity: ReplyReviewEntity, user: User) = reviewReply {
            id = replyReviewEntity.id!!
            reviewId = replyReviewEntity.review.id!!
            userDisplayName = user.firstName
            userAvatar = user.profilePhoto
            body = replyReviewEntity.body
        }

        fun buildReviewEntityFromReviewDto(reviewEntity: ReviewEntity, request: UpdateReviewRequest) {
            request.body.whenNotEmpty { reviewEntity.body = it }
            request.rating.whenGreaterThanZero { reviewEntity.rating = it }
        }

        fun transformCreateRatingConfigRequestToRatingConfigEntity(
            request: CreateRatingConfigRequest
        ): RatingConfigEntity {
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
