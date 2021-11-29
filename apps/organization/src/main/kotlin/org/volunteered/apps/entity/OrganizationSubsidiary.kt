package org.volunteered.apps.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
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
class OrganizationSubsidiary(
    @Id
    @GeneratedValue
    var id: Long? = null,

    var name: String? = null,

    @Email
    var email: String? = null,

    var city: String,

    @Size(max = 2)
    var country: String,

    var phones: Set<String> = HashSet(),

    var description: String? = null,
)