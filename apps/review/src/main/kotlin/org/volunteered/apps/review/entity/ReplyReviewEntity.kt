package org.volunteered.apps.review.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "reply-review")
class ReplyReviewEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    var review: ReviewEntity,

    @Column(name = "user_id")
    var userId : Long,

    @Column(name = "user_display_name")
    var userDisplayName : String,

    @Column(name = "user_avatar")
    var userAvatar : String? = null,

    var body : String
)