package org.volunteered.apps.recommendation.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.Size

@Entity
@Table(name = "recommendation_request")
class RecommendationEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @Size(max = 255)
    @Column(name = "position_held")
    var positionHeld: String,

    var duration: Int,

    @Size(max = 255)
    @Column(name = "recommender_position")
    var recommenderPosition: String,

    @Size(max = 2000)
    var body: String,
)