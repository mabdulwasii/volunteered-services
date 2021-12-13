package org.volunteered.apps.organization.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.organization.entity.OrganizationEntity

@Repository
interface OrganizationRepository : JpaRepository<OrganizationEntity, Long> {
    fun existsByEmail(email: String): Boolean
    fun findByNameLike(name: String) : List<OrganizationEntity>
}
