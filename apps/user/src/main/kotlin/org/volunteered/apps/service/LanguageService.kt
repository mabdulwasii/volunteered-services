package org.volunteered.apps.service

import org.volunteered.apps.entity.Language

interface LanguageService {
    fun findByName(name: String): Language?
    fun findByNameIn(names: List<String>): List<Language>
}