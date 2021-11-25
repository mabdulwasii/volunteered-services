package org.volunteered.apps.utilities

import org.springframework.stereotype.Component
import org.volunteered.apps.entity.Language
import org.volunteered.apps.entity.Skill
import org.volunteered.apps.entity.UserDetails
import org.volunteered.apps.entity.enumeraion.Gender
import org.volunteered.apps.service.LanguageService
import org.volunteered.apps.service.SkillService
import org.volunteered.libs.common.v1.User
import org.volunteered.libs.common.v1.user
import org.volunteered.libs.common.v1.websiteAndSocialMediaUrls
import org.volunteered.libs.user.v1.CreateUserRequest

@Component
class Utils(
    private val skillService: SkillService,
    private val languageService: LanguageService
) {

    fun convertCreateUserRequestToUserDetails(request: CreateUserRequest): UserDetails {
        val userDetails = UserDetails(
            firstName = request.firstName,
            lastName = request.lastName,
            phone = request.phone,
            email = request.email,
            country = request.country
        )
        return userDetails
    }

    fun convertUserDetailsToUser(userDetails: UserDetails?) = user {
        userDetails?.id
        userDetails?.firstName
        userDetails?.lastName
        userDetails?.phone
        userDetails?.email
        userDetails?.country
        userDetails?.city
        userDetails?.bio
        userDetails?.gender?.name
        userDetails?.mainSkills?.map { it.name }
        userDetails?.otherSkills?.map { it.name }
        userDetails?.spokenLanguages?.map { it.name }
        userDetails?.cv
        userDetails?.portfolio
        userDetails?.profilePhoto
        websiteAndSocialMediaUrls {
            userDetails?.website
            userDetails?.linkedin
            userDetails?.facebook
            userDetails?.twitter
            userDetails?.skype
            userDetails?.github
        }
    }

    fun convertStringToSkillSet(skillsList: List<String>): HashSet<Skill> {

        val mainSkills = hashSetOf<Skill>()

        val retrievedSkills = skillService.findByNameIn(skillsList)
        mainSkills += retrievedSkills
        return mainSkills
    }

    fun convertStringToLanguageSet(languageList: List<String>): HashSet<Language> {

        val mainLanguages = hashSetOf<Language>()

        val retrievedLanguages = languageService.findByNameIn(languageList)
        mainLanguages += retrievedLanguages
        return mainLanguages
    }

    fun buildUserDetailsWithUserUpdateRequest(
        user: User,
        userDetails: UserDetails?,
        mainSkills: HashSet<Skill>,
        otherSkills: HashSet<Skill>,
        spokenLanguages: HashSet<Language>
    ): UserDetails? {

        user.firstName?.let { userDetails?.firstName = user.firstName }
        user.lastName?.let { userDetails?.lastName = user.lastName }
        user.phone?.let { userDetails?.phone = user.phone }
        user.email?.let { userDetails?.email = user.email }
        user.country?.let { userDetails?.country = user.country }
        user.city?.let { userDetails?.city = user.city }
        user.bio?.let { userDetails?.bio = user.bio }
        user.gender?.name?.let { userDetails?.gender = Gender.valueOf(user.gender.name) }
        mainSkills.let { userDetails?.mainSkills = mainSkills }
        otherSkills.let { userDetails?.otherSkills = otherSkills }
        spokenLanguages.let { userDetails?.spokenLanguages = spokenLanguages }
        user.cv?.let { userDetails?.cv = user.cv }
        user.portfolio?.let { userDetails?.portfolio = user.portfolio }
        user.profilePhoto?.let { userDetails?.profilePhoto = user.profilePhoto }
        user.webAndSocialUrls?.website?.let { userDetails?.website = user.webAndSocialUrls.website }
        user.webAndSocialUrls?.linkedin?.let { userDetails?.linkedin = user.webAndSocialUrls.linkedin }
        user.webAndSocialUrls?.facebook?.let { userDetails?.facebook = user.webAndSocialUrls.facebook }
        user.webAndSocialUrls?.twitter?.let { userDetails?.twitter = user.webAndSocialUrls.twitter }
        user.webAndSocialUrls?.skype?.let { userDetails?.skype = user.webAndSocialUrls.skype }
        user.webAndSocialUrls?.github?.let { userDetails?.github = user.webAndSocialUrls.github }

        return userDetails
    }

}