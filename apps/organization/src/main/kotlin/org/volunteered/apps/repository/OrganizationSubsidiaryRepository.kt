package org.volunteered.apps.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.entity.OrganizationSubsidiary

@Repository
interface OrganizationSubsidiaryRepository : JpaRepository<OrganizationSubsidiary, Long> {
    fun findByEmail(email: String): OrganizationSubsidiary?
    fun existsByEmail(email: String): Boolean
}