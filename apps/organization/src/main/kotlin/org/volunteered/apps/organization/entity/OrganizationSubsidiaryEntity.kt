package org.volunteered.apps.organization.entity

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.Size

@Entity
@Table(
    name = "organization_subsidiary",
    indexes = [
        Index(name = "index_email", columnList = "email", unique = true),
        Index(name = "index_name", columnList = "name", unique = true)
    ]
)
class OrganizationSubsidiaryEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,

    var name: String? = null,

    @Email
    var email: String? = null,

    var city: String,

    @Size(max = 2)
    var country: String,

    var phone: String? = null,

    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    var parent: OrganizationEntity
)
