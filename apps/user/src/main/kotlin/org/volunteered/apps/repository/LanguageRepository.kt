package org.volunteered.apps.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.entity.Language

@Repository
interface LanguageRepository : JpaRepository<Language, Long> {
    fun findByName(name: String): Language?
    fun findByNameIn(names: List<String>): List<Language>
}