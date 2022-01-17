package org.volunteered.apps.recommendation.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.recommendation.entity.RecommendationRequestEntity

@Repository
interface RecommendationRequestRepository : JpaRepository<RecommendationRequestEntity, Long> {
    fun findAllByOrganizationSubsidiaryId(organizationSubsidiary: Long, pageable: Pageable) : Page<RecommendationRequestEntity>
}
