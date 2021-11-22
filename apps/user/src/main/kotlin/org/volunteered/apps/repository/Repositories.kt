package org.volunteered.apps.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.entity.Language
import org.volunteered.apps.entity.Skill
import org.volunteered.apps.entity.UserDetails

@Repository
interface UserDetailsRepository : JpaRepository<UserDetails, Long> {
    fun findByEmail(email: String): UserDetails?
    fun existsByEmail(email: String): Boolean
}

@Repository
interface SkillRepository : JpaRepository<Skill, Long> {
    fun findByName(name: String): Skill?
}

@Repository
interface LanguageRepository : JpaRepository<Language, Long> {
    fun findByName(name: String): Language?
}