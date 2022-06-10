package org.volunteered.apps.recommendation.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.Size

@Entity
@Table(name = "recommendation")
class RecommendationEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @Column(name = "organization_subsidiary_id")
    var organizationSubsidiaryId: Long,

    @Size(max = 255)
    @Column(name = "position_held")
    var positionHeld: String,

    var duration: Int,

    @Size(max = 255)
    @Column(name = "recommender_position_id")
    var recommenderPositionId: Long,

    @Size(max = 2000)
    var body: String,

    var userId: Long
)
