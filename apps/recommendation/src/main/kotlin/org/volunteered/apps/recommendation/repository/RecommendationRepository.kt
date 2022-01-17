package org.volunteered.apps.recommendation.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.recommendation.entity.RecommendationEntity

@Repository
interface RecommendationRepository : JpaRepository<RecommendationEntity, Long>
