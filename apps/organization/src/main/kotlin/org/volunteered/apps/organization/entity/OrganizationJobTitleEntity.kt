package org.volunteered.apps.organization.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    name = "organization_job_title",
    indexes = [Index(name = "index_organization_id", columnList = "organization_id")]
)
class OrganizationJobTitleEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @Column(name = "organization_id")
    var organizationId: Long,

    @Column(name = "job_title")
    var title: String
)
