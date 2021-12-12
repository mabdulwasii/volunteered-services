package org.volunteered.apps.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.entity.ReplyReviewEntity

@Repository
interface ReplyReviewRepository : JpaRepository<ReplyReviewEntity, Long> {
}