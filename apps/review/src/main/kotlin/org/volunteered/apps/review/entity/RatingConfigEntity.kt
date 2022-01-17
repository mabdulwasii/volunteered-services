package org.volunteered.apps.review.entity

import org.volunteered.libs.proto.review.v1.RatingType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "review")
class RatingConfigEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_type")
    var ratingType: RatingType,

    @Column(name = "weight")
    var weight: Int,
)
