package org.volunteered.apps.review.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "review")
class RatingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    var rating : Int,

    @Column(name = "organization_subsidiary_id")
    var organizationSubsidiaryId  : Long,

    @Column(name = "unverified_rating_count")
    var unverifiedRatingCount : Int = 0,

    @Column(name = "verified_rating_count")
    var verifiedRatingCount : Int = 0,
)