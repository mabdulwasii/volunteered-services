package org.volunteered.apps.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import org.volunteered.apps.entity.ReviewEntity

@Repository
interface ReviewRepository : JpaRepository<ReviewEntity, Long>, JpaSpecificationExecutor<ReviewEntity> {
    fun existsByUserIdAndOrganizationSubsidiaryId(
        userId: String, organizationSubsidiaryId:
        Long
    ): Boolean
}