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
        Index(name = "index_user_id", columnList = "user_id"),
        Index(
            name = "index_organization_subsidiary_id",
            columnList = "organization_subsidiary_id"
        ),
        Index(
            name = "index_user_id_organization_subsidiary_id",
            columnList = "user_id, organization_subsidiary_id",
            unique = true
        )
    ]
)
class ReviewEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    var rating: Int = 0,

    var body: String? = null,

    @Column(name = "user_id")
    var userId: String,

    @Column(name = "organization_subsidiary_id")
    var organizationSubsidiaryId: Long,

    @Column(name = "helpful_count")
    var helpfulCount: Long? = null,

    var verified: Boolean = false,

    var anonymous: Boolean = false,
)
