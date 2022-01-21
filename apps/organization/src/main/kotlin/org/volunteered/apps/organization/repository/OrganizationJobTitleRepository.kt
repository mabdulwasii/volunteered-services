package org.volunteered.apps.organization.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.organization.entity.OrganizationJobTitleEntity

@Repository
interface OrganizationJobTitleRepository : JpaRepository<OrganizationJobTitleEntity, Long> {
    fun findAllByOrganizationId(organizationId: Long): List<OrganizationJobTitleEntity>
}
