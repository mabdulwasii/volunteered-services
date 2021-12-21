package org.volunteered.apps.organization.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.organization.entity.OrganizationSubsidiaryEntity

@Repository
interface OrganizationSubsidiaryRepository : JpaRepository<OrganizationSubsidiaryEntity, Long> {
    fun findByEmail(email: String): OrganizationSubsidiaryEntity?
    fun existsByEmail(email: String): Boolean
}
