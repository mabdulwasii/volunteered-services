package org.volunteered.apps.recommendation.entity


import org.volunteered.libs.proto.recommendation.v1.RequestStatus
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.Size

@Entity
@Table(name = "recommendation_request")
class RecommendationRequestEntity (
    @Id
    @GeneratedValue
    var id: Long? = null,

    @Size(max = 255)
    @Column(name = "user_id")
    var userId: Long,

    @Column(name = "organization_subsidiary_id")
    var organizationSubsidiaryId : Long,

    @Size(max = 255)
    @Column(name = "status")
    var status: RequestStatus
)
