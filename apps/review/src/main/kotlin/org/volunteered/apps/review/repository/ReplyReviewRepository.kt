package org.volunteered.apps.review.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.review.entity.ReplyReviewEntity

@Repository
interface ReplyReviewRepository : JpaRepository<ReplyReviewEntity, Long> {
    fun findAllByReviewId(reviewId: Long): List<ReplyReviewEntity>
}
