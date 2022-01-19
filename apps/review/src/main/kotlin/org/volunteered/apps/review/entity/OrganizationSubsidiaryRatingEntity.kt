package org.volunteered.apps.review.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    name = "review",
    indexes = [
        Index(
            name = "index_organization_subsidiary_id",
            columnList = "organization_subsidiary_id"
        )
    ]
)
class OrganizationSubsidiaryRatingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    var rating: Double,

    @Column(name = "organization_subsidiary_id", unique = true)
    var organizationSubsidiaryId: Long,

    @Column(name = "unverified_rating_count")
    var unverifiedRatingCount: Int = 0,

    @Column(name = "verified_rating_count")
    var verifiedRatingCount: Int = 0,
)
