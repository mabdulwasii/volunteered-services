package org.volunteered.apps.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "review")
class ReviewEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(name = "user_id")
    var userId : Long,

    @Column(name = "organization_id")
    var organizationId  : Long,

    @Column(name = "organization_subsidiary_id")
    var organizationSubsidiaryId  : Long,

    @Column(name = "user_display_name")
    var userDisplayName : String,

    @Column(name = "user_avatar")
    var userAvatar : String? = null,

    var rating : Int,

    var body : String,

    @Column(name = "helpful_count")
    var helpfulCount : Long ? = null,

    @Column(name = "organization_subsidiary_city")
    var organizationSubsidiaryCity : String,

    var verified : Boolean? = false
)