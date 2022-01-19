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
    name = "helpful_review",
    indexes = [
        Index(
            name = "index_user_id_review_id",
            columnList = "user_id, review_id",
            unique = true
        )
    ]
)
class UserHelpfulReviewEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(name = "user_id")
    var userId: Long,

    @Column(name = "review_id")
    var reviewId: Long,
)
