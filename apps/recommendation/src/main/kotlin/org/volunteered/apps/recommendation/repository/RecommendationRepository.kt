package org.volunteered.apps.recommendation.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.recommendation.entity.RecommendationEntity

@Repository
interface RecommendationRepository : JpaRepository<RecommendationEntity, Long> {
    fun findAllByOrganizationSubsidiaryId(organizationSubsidiaryId: Long, pageable: Pageable): Page<RecommendationEntity>
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<RecommendationEntity>
    fun findAllByUserIdInAndOrganizationSubsidiaryId(
        userIds: List<Long>,
        organizationSubsidiaryId: Long
    ): List<RecommendationEntity>
}
