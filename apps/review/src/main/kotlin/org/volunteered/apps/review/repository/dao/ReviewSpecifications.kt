package org.volunteered.apps.review.repository.dao

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.volunteered.apps.review.entity.ReviewEntity
import org.volunteered.apps.review.entity.ReviewEntity_
import org.volunteered.libs.proto.review.v1.ReviewsFilterRequest
import org.volunteered.libs.proto.review.v1.ReviewsSortRequest

class ReviewSpecifications {
    companion object {
        /*private fun containsOrganizationSubsidiaryCity(organizationSubsidiaryCity: String): Specification<ReviewEntity> {
            return Specification<ReviewEntity> { root, _, builder ->
                if (organizationSubsidiaryCity.isNotBlank()) {
                    builder.like(
                        builder.lower(root.get(ReviewEntity_.organizationSubsidiaryCity)),
                        "%${organizationSubsidiaryCity.lowercase()}%"
                    )
                } else {
                    null
                }
            }
        }*/

        private fun hasRating(rating: Int): Specification<ReviewEntity> {
            return Specification<ReviewEntity> { root, _, builder ->
                if (rating > 0) {
                    builder.equal(root.get(ReviewEntity_.rating), "$rating")
                } else {
                    null
                }
            }
        }

        private fun isVerified(verified: Boolean): Specification<ReviewEntity> {
            return Specification<ReviewEntity> { root, _, builder ->
                if (verified) {
                    builder.isTrue(root.get(ReviewEntity_.verified))
                } else {
                    null
                }
            }
        }

        private fun hasOrganizationSubsidiaryId(organizationSubsidiaryId: Long): Specification<ReviewEntity> {
            return Specification<ReviewEntity> { root, _, builder ->
                if (organizationSubsidiaryId > 0) {
                    builder.equal(root.get(ReviewEntity_.organizationSubsidiaryId), "$organizationSubsidiaryId")
                } else {
                    null
                }
            }
        }

        private fun hasOrganizationIdIn(organizationSubsidiaryIds: List<Long>): Specification<ReviewEntity> {
            return Specification<ReviewEntity> { root, _, builder ->
                if (organizationSubsidiaryIds.isNotEmpty()) {
                    builder.isTrue(root.get(ReviewEntity_.organizationSubsidiaryId).`in`(organizationSubsidiaryIds))
                } else {
                    null
                }
            }
        }

        private fun hasUserId(userId: Long): Specification<ReviewEntity> {
            return Specification<ReviewEntity> { root, _, builder ->
                if (userId > 0) {
                    builder.equal(root.get(ReviewEntity_.userId), "$userId")
                } else {
                    null
                }
            }
        }

        fun buildReviewSpecificationForOrganization(
            filter: ReviewsFilterRequest,
            organizationSubsidiaryIds: List<Long>
        ): Specification<ReviewEntity> {
            val verified = filter.verified
            val rating = filter.rating

            return Specification
                .where(isVerified(verified.value))
                .and(hasRating(rating.value))
                .and(hasOrganizationIdIn(organizationSubsidiaryIds))
        }

        fun buildReviewSpecificationForOrganizationSubsidiary(
            filter: ReviewsFilterRequest,
            organizationSubsidiaryId: Long
        ): Specification<ReviewEntity> {
            val verified = filter.verified
            val rating = filter.rating

            return Specification
                .where(isVerified(verified.value))
                .and(hasRating(rating.value))
                .and(hasOrganizationSubsidiaryId(organizationSubsidiaryId))
        }

        fun buildReviewSpecificationForUser(
            filter: ReviewsFilterRequest,
            userId: Long
        ): Specification<ReviewEntity> {
            val verified = filter.verified
            val rating = filter.rating

            return Specification
                .where(isVerified(verified.value))
                .and(hasRating(rating.value))
                .and(hasUserId(userId))
        }

        fun buildEntitySort(sorting: ReviewsSortRequest): Sort {
            val requestDirection = sorting.direction
            val direction = Sort.Direction.fromString(requestDirection.name)
            val sort = sorting.sort
            val properties = mutableListOf<Sort.Order>()

            if (sort.hasRating()) {
                properties.add(Sort.Order(direction, "rating"))
            }
            if (sort.hasHelpful()) {
                properties.add(Sort.Order(direction, "helpfulCount"))
            }

            return Sort.by(properties)
        }
    }
}
