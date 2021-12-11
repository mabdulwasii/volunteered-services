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

    @Column(name = "user_display_name")
    var userDisplayName : String,

    @Column(name = "user_avatar")
    var userAvatar : String,

    var rating : Int,

    var body : String,

    @Column(name = "helpful_count")
    var helpfulCount : Long,

    @Column(name = "organization_subsidiary_city")
    var organizationSubsidiaryCity : String,

    var verified : Boolean
)