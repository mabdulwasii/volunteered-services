package org.volunteered.apps.review.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.review.entity.RatingEntity

@Repository
interface RatingRepository : JpaRepository<RatingEntity, Long> {
    fun findByOrganizationSubsidiaryId(organizationSubsidiaryId: Long): RatingEntity?
}
