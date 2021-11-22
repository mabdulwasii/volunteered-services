package org.volunteered.apps.service

import org.volunteered.apps.entity.Language
import org.volunteered.apps.entity.Skill
import org.volunteered.apps.entity.UserDetails
import java.util.*

interface UserDetailsService {
    fun findByEmail(email: String): UserDetails?
    fun save(userDetails: UserDetails): UserDetails?
    fun existsByEmail(email: String): Boolean
}


interface SkillService {
    fun findByName(name: String): Skill?
    fun findById(id: Long): Optional<Skill>

}

interface LanguageService {
    fun findByName(name: String) : Language?
}