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

    var rating : Int = 0,

    var body : String? = null,

    @Column(name = "user_id")
    var userId : String,

    @Column(name = "organization_subsidiary_id")
    var organizationSubsidiaryId  : Long,

    @Column(name = "helpful_count")
    var helpfulCount : Long ?= null,

    var verified : Boolean = false
)