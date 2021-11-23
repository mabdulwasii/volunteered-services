package org.volunteered.apps.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.volunteered.apps.entity.*

@DataJpaTest
class RepositoriesTest @Autowired constructor(
    val entityManager : TestEntityManager,
    val userDetailsRepository: UserDetailsRepository,
    val skillRepository: SkillRepository,
    val languageRepository: LanguageRepository){


    lateinit var javaSkill : Skill
    lateinit var kotlinSkill : Skill
    lateinit var writingSkill : Skill
    lateinit var criticalThinking : Skill
    lateinit var userDetails : UserDetails
    lateinit var english : Language
    lateinit var french : Language
    lateinit var userSocialMedia : UserSocialMedia


    @BeforeEach
    fun setUp(){
        javaSkill = Skill("Java")
        kotlinSkill = Skill("Kotlin")
        writingSkill = Skill("Writing")
        criticalThinking = Skill("Critical thinking")

        userSocialMedia = UserSocialMedia("linked_url", "facebook_url",
            "twitter_url", "skype_url", "website_url")

        french = Language("french")
        english = Language("english")

        val mainSkills = setOf<Skill>(javaSkill, kotlinSkill)
        val otherSkills = setOf<Skill>(writingSkill, criticalThinking)


        val spokenLanguages = setOf<Language>(english, french)

        userDetails = UserDetails("Paul", "Ben", "23470647888", "m@gmail.com",
            "Nigeria", "Lagos", "This is my bio", Gender.MALE,  mainSkills, otherSkills, spokenLanguages,
            "resume_url", "portfolio_url", "profile_photo_url", userSocialMedia)

    }

    @Test
    fun shouldFindById() {

        entityManager.persist(javaSkill)
        entityManager.persist(kotlinSkill)
        entityManager.persist(writingSkill)
        entityManager.persist(criticalThinking)

        entityManager.persist(french)
        entityManager.persist(english)

        entityManager.persist(userSocialMedia)

        entityManager.persist(userDetails)

        entityManager.flush()

        val foundUserDetails = userDetailsRepository.findByEmail(userDetails.email)

        assertEquals(userDetails, foundUserDetails)

    }


}