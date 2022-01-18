package org.volunteered.apps.review.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import org.volunteered.apps.review.entity.ReviewEntity

@Repository
interface ReviewRepository : JpaRepository<ReviewEntity, Long>, JpaSpecificationExecutor<ReviewEntity> {
    fun existsByUserIdAndOrganizationSubsidiaryId(
        userId: String, organizationSubsidiaryId:
        Long
    ): Boolean
    fun findAllByOrganizationSubsidiaryId(organizationSubsidiaryId: Long, pageable: Pageable): Page<ReviewEntity>
    fun findAllByOrganizationId(organizationId: Long, pageable: Pageable): Page<ReviewEntity>
    fun findByUserIdAndOrganizationId(userId: String, organizationId: Long): ReviewEntity?
    fun existsByUserIdAndOrganizationSubsidiaryId(userId: String, organizationSubsidiaryId: Long): Boolean
}
