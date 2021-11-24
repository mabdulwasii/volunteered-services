package org.volunteered.apps.service

import org.volunteered.apps.entity.Skill
import java.util.*

interface SkillService {
    fun findByName(name: String): Skill?
    fun findById(id: Long): Optional<Skill>
    fun findByNameIn(names: List<String>): List<Skill>


}