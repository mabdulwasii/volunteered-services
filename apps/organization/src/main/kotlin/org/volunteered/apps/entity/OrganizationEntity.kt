package org.volunteered.apps.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.Size

@Entity
@Table(
    name = "organization",
    indexes = [
        Index(name = "index_email", columnList = "email", unique = true),
        Index(name = "index_name", columnList = "name", unique = true)
    ]
)
class OrganizationEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @Size(max = 256)
    var name: String,

    @Email
    var email: String,

    @Size(max = 2000)
    var bio: String,

    @OneToOne(targetEntity = OrganizationSubsidiary::class)
    var hq: OrganizationSubsidiary,

    var logo: String? = null,

    var phones: Set<String> = HashSet(),

    var website: String? = null,

    var linkedin: String? = null,

    var facebook: String? = null,

    var twitter: String? = null,

    var skype: String? = null,

    var github: String? = null,

    var founded: Int? = null,

    var industry: String? = null,

    var founder: String? = null,

    @Column(name = "number_of_employees")
    var numberOfEmployees: Int? = null,

    @ManyToMany(targetEntity = Benefit::class, fetch = FetchType.LAZY)
    @JoinTable(name = "organization_benefit")
    var benefits: Set<Benefit> = HashSet()
)