package org.volunteered.apps.service.impl

import org.springframework.stereotype.Service
import org.volunteered.apps.entity.Skill
import org.volunteered.apps.repository.SkillRepository
import org.volunteered.apps.service.SkillService
import java.util.*

@Service
class SkillServiceImpl(private val skillRepository: SkillRepository) : SkillService {
    override fun findByName(name: String): Skill? {
        return skillRepository.findByName(name)
    }

    override fun findByNameIn(names: List<String>): List<Skill> {
        return skillRepository.findByNameIn(names)
    }

    override fun findById(id: Long): Optional<Skill> {
        return skillRepository.findById(id)
    }
}