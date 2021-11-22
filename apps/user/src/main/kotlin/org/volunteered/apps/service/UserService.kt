package org.volunteered.apps.service

import net.devh.boot.grpc.server.service.GrpcService
import org.volunteered.apps.entity.*
import org.volunteered.libs.common.v1.User
import org.volunteered.libs.common.v1.user
import org.volunteered.libs.common.v1.userSocialMedia
import org.volunteered.libs.user.v1.CreateUserRequest
import org.volunteered.libs.user.v1.GetRequest
import org.volunteered.libs.user.v1.UpdateUserRequest
import org.volunteered.libs.user.v1.UserServiceGrpcKt

@GrpcService
class UserService(private val userDetailsService: UserDetailsService,
                  private val skillService: SkillService,
                  private val languageService: LanguageService
                  ): UserServiceGrpcKt.UserServiceCoroutineImplBase() {

    override suspend fun createUser(request: CreateUserRequest): User {

        val email = request.email

        val existsByEmail = userDetailsService.existsByEmail(email)

        if (!existsByEmail) {

            val userDetails = UserDetails(
                request.firstName,
                request.lastName,
                request.phone,
                request.email,
                request.country
            )

            val savedUserDetails = userDetailsService.save(userDetails)

            val userSocialMedia = savedUserDetails?.userSocialMedia

            return user {
                savedUserDetails?.email
                savedUserDetails?.firstName
                savedUserDetails?.lastName
                savedUserDetails?.phone
                savedUserDetails?.email
                savedUserDetails?.country
                savedUserDetails?.city
                savedUserDetails?.bio
                savedUserDetails?.gender?.name
                savedUserDetails?.mainSkills
                savedUserDetails?.otherSkills
                savedUserDetails?.spokenLanguages
                savedUserDetails?.resumeUrl
                savedUserDetails?.portfolioUrl
                userSocialMedia {
                    userSocialMedia?.linkedinUrl
                    userSocialMedia?.facebookUrl
                    userSocialMedia?.twitterUrl
                    userSocialMedia?.skypeUrl
                    userSocialMedia?.websiteUrl
                }
            }
        }

        return user {}
    }

    override suspend fun update(request: UpdateUserRequest): User {

        val user = request.user
        var email = request.email

        val userDetails = userDetailsService.findByEmail(email)

        val mainSkillsList = user.mainSkillsList.toList()
        val currentMainSkills = userDetails?.mainSkills

        val mainSkills = convertStringToSkillSet(currentMainSkills, mainSkillsList)

        val otherSkillsList = user.otherSkillsList.toList()
        val currentOtherSkills = userDetails?.otherSkills

        val otherSkills = convertStringToSkillSet(currentOtherSkills, otherSkillsList)

        val spokenLanguageList = user.spokenLanguagesList.toList()
        val currentSpokenLanguages = userDetails?.spokenLanguages

        val spokenLanguages = convertStringToLanguageSet(currentSpokenLanguages, spokenLanguageList)

        var userSocialMedia = userDetails?.userSocialMedia
        userSocialMedia = coverToUserMediaEntity(userSocialMedia, user)

        userDetails?.firstName = user.firstName
        userDetails?.lastName = user.lastName
        userDetails?.phone = user.phone
        userDetails?.email = user.email
        userDetails?.country = user.country
        userDetails?.city = user.city
        userDetails?.bio = user.bio
        userDetails?.gender = Gender.valueOf(user.gender.name)
        userDetails?.mainSkills = mainSkills
        userDetails?.otherSkills = otherSkills
        userDetails?.spokenLanguages = spokenLanguages
        userDetails?.resumeUrl = user.resumeUrl
        userDetails?.portfolioUrl = user.portfolioUrl
        userDetails?.profilePhotoUrl = user.profilePhotoUrl
        userDetails?.userSocialMedia = userSocialMedia

        val updatedUserDetails = userDetails?.let { userDetailsService.save(it) }

        return user {
            updatedUserDetails?.email
            updatedUserDetails?.firstName
            updatedUserDetails?.lastName
            updatedUserDetails?.phone
            updatedUserDetails?.email
            updatedUserDetails?.country
            updatedUserDetails?.city
            updatedUserDetails?.bio
            updatedUserDetails?.gender?.name
            updatedUserDetails?.mainSkills
            updatedUserDetails?.otherSkills
            updatedUserDetails?.spokenLanguages
            updatedUserDetails?.resumeUrl
            updatedUserDetails?.portfolioUrl
            userSocialMedia {
                userSocialMedia?.linkedinUrl
                userSocialMedia?.facebookUrl
                userSocialMedia?.twitterUrl
                userSocialMedia?.skypeUrl
                userSocialMedia?.websiteUrl
            }
        }



    }

    private fun coverToUserMediaEntity(
        userSocialMedia: UserSocialMedia?,
        user: User
    ): UserSocialMedia? {
        userSocialMedia?.facebookUrl = user.userSocialMedia.facebookUrl
        userSocialMedia?.linkedinUrl = user.userSocialMedia.linkedinUrl
        userSocialMedia?.twitterUrl = user.userSocialMedia.twitterUrl
        userSocialMedia?.skypeUrl = user.userSocialMedia.skypeUrl
        userSocialMedia?.websiteUrl = user.userSocialMedia.websiteUr

        return userSocialMedia
    }

    private fun convertStringToSkillSet(skillSet: Set<Skill>?, skillsList: List<String>): HashSet<Skill> {

        val mainSkills = hashSetOf<Skill>()
        skillSet?.let { mainSkills.addAll(it) }

        skillsList.forEach { mainSkill ->
            val skill = skillService.findByName(mainSkill)
            skill?.let { mainSkills.add(it) }
        }
        return mainSkills
    }

    private fun convertStringToLanguageSet(languageSet: Set<Language>?, languageList: List<String>): HashSet<Language> {

        val mainLanguages = hashSetOf<Language>()
        languageSet?.let { mainLanguages.addAll(it) }

        languageList.forEach { language ->
            val userLanguage = languageService.findByName(language)
            userLanguage?.let { mainLanguages.add(it) }
        }
        return mainLanguages
    }

    override suspend fun get(request: GetRequest): User {
        val email = request.email
        val userDetails = userDetailsService.findByEmail(email)

        return user {
            userDetails?.email
            userDetails?.firstName
            userDetails?.lastName
            userDetails?.phone
            userDetails?.email
            userDetails?.country
            userDetails?.city
            userDetails?.bio
            userDetails?.gender
            userDetails?.resumeUrl
            userDetails?.portfolioUrl
            userDetails?.profilePhotoUrl
            userDetails?.userSocialMedia
            userDetails?.mainSkills
            userDetails?.otherSkills
            userDetails?.spokenLanguages
            userDetails?.userSocialMedia
        }

    }

    }