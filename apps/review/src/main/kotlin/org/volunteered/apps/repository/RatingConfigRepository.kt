package org.volunteered.apps.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.entity.RatingConfigEntity
import org.volunteered.libs.proto.review.v1.RatingType

@Repository
interface RatingConfigRepository : JpaRepository<RatingConfigEntity, Long> {
    fun findByRatingType(ratingType: RatingType) : RatingConfigEntity
    fun findById(id: Int) : RatingConfigEntity?
}