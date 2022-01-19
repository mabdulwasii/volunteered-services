package org.volunteered.apps.review.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import org.volunteered.apps.review.entity.ReviewEntity
import org.volunteered.apps.review.entity.UserHelpfulReviewEntity

@Repository
interface UserHelpfulReviewRepository : JpaRepository<UserHelpfulReviewEntity, Long>, JpaSpecificationExecutor<ReviewEntity> {
    fun existsByUserIdAndReviewId(userId: Long, reviewId: Long): Boolean
}
