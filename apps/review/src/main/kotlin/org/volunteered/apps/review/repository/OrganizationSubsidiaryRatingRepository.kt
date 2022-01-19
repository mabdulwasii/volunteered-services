package org.volunteered.apps.review.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.review.entity.OrganizationSubsidiaryRatingEntity

@Repository
interface OrganizationSubsidiaryRatingRepository : JpaRepository<OrganizationSubsidiaryRatingEntity, Long> {
    fun findByOrganizationSubsidiaryId(organizationSubsidiaryId: Long): OrganizationSubsidiaryRatingEntity?
}
