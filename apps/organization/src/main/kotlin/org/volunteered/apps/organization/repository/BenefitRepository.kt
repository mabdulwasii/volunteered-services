package org.volunteered.apps.organization.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.organization.entity.Benefit

@Repository
interface BenefitRepository : JpaRepository<Benefit, Long> {
    fun findByNameIn(names: List<String>): List<Benefit>
}
