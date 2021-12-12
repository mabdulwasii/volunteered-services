package org.volunteered.apps.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.entity.ReviewEntity

@Repository
interface ReviewRepository : JpaRepository<ReviewEntity, Long> {
    fun findAllByOrganizationSubsidiaryId(organizationSubsidiaryId : Long, pageable: Pageable) : Page<ReviewEntity>

    fun findAllByOrganizationId(organizationId : Long, pageable: Pageable) : Page<ReviewEntity>
}