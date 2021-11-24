package org.volunteered.apps.service.impl

import org.springframework.stereotype.Service
import org.volunteered.apps.entity.Language
import org.volunteered.apps.repository.LanguageRepository
import org.volunteered.apps.service.LanguageService

@Service
class LanguageServiceImpl(private val languageRepository: LanguageRepository) : LanguageService {
    override fun findByName(name: String): Language? {
        return languageRepository.findByName(name)
    }

    override fun findByNameIn(names: List<String>): List<Language> {
        return languageRepository.findByNameIn(names)
    }

}