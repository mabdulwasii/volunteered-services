package org.volunteered.apps.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.user.entity.Skill

@Repository
interface SkillRepository : JpaRepository<Skill, Long> {
    fun findByNameIn(names: List<String>): List<Skill>
}
