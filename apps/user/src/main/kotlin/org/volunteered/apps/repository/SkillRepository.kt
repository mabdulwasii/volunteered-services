package org.volunteered.apps.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.entity.Skill

@Repository
interface SkillRepository : JpaRepository<Skill, Long> {
    fun findByName(name: String): Skill?
    fun findByNameIn(names: List<String>): List<Skill>
}