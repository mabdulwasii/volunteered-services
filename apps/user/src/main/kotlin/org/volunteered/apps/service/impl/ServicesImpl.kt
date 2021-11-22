package org.volunteered.apps.service.impl

import org.springframework.stereotype.Service
import org.volunteered.apps.entity.Language
import org.volunteered.apps.entity.Skill
import org.volunteered.apps.entity.UserDetails
import org.volunteered.apps.repository.LanguageRepository
import org.volunteered.apps.repository.SkillRepository
import org.volunteered.apps.repository.UserDetailsRepository
import org.volunteered.apps.service.LanguageService
import org.volunteered.apps.service.SkillService
import java.util.*

@Service
class UserDetailsServiceImpl(private val userDetailsRepository: UserDetailsRepository) :
    org.volunteered.apps.service.UserDetailsService {

    override fun findByEmail(email: String): UserDetails? {
        return userDetailsRepository.findByEmail(email)
    }

    override fun save(userDetails: UserDetails): UserDetails? {
        return userDetailsRepository.save(userDetails)
    }

    override fun existsByEmail(email: String): Boolean {
        return userDetailsRepository.existsByEmail(email)
    }
}

@Service
class SkillServiceImpl(private val skillRepository: SkillRepository) : SkillService {
    override fun findByName(name: String): Skill? {
        return skillRepository.findByName(name)
    }

    override fun findById(id: Long): Optional<Skill> {
        return skillRepository.findById(id)
    }
}

@Service
class LanguageServiceImpl(private val languageRepository: LanguageRepository) : LanguageService {
    override fun findByName(name: String): Language? {
        return languageRepository.findByName(name)
    }

}