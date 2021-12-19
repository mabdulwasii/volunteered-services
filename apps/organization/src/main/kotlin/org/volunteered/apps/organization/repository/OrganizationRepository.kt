package org.volunteered.apps.organization.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.organization.entity.OrganizationEntity

@Repository
interface OrganizationRepository : JpaRepository<OrganizationEntity, Long> {
    fun findByEmail(email: String): OrganizationEntity?
    fun existsByEmail(email: String): Boolean
}