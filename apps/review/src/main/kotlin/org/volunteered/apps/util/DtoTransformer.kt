package org.volunteered.apps.util

import org.volunteered.apps.entity.ReviewEntity
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.review.v1.Review
import org.volunteered.libs.proto.review.v1.WriteReviewRequest
import org.volunteered.libs.proto.review.v1.review

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
                organizationSubsidiaryId = organizationSubsidiary.id
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

        fun transformReviewEntityListToReviewDtoList(reviewEntityList: List<ReviewEntity>): List<Review> {
            val reviewDtoList = mutableListOf<Review>()

            reviewEntityList.forEach {
                val reviewDto = transformReviewEntityToReviewDto(it)
                reviewDtoList.add(reviewDto)
            }

            return reviewDtoList
        }
    }
}