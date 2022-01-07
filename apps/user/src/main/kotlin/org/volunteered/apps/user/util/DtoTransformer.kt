package org.volunteered.apps.user.util

import org.volunteered.apps.user.entity.UserEntity
import org.volunteered.libs.core.extension.whenNotEmpty
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.common.v1.user
import org.volunteered.libs.proto.common.v1.websiteAndSocialMediaUrls
import org.volunteered.libs.proto.user.v1.CreateUserRequest

class DtoTransformer {
    companion object {
        fun transformCreateUserRequestToUserEntity(request: CreateUserRequest): UserEntity {
            return UserEntity(
                firstName = request.firstName,
                lastName = request.lastName,
                email = request.email,
                country = request.country
            )
        }

        fun transformUserEntityToUserDto(userEntity: UserEntity) : User {
            val websiteAndSocialMediaUrls =  websiteAndSocialMediaUrls {
                userEntity.website?.let { website = it }
                userEntity.linkedin?.let { linkedin = it }
                userEntity.facebook?.let { facebook = it }
                userEntity.twitter?.let { twitter = it }
                userEntity.skype?.let { skype = it }
                userEntity.github?.let { github = it }
            }

            return user {
                id = userEntity.id!!
                firstName = userEntity.firstName
                lastName = userEntity.lastName
                email = userEntity.email
                country = userEntity.country
                userEntity.phone?.let { phone = it }
                userEntity.city?.let { city = it }
                userEntity.bio?.let { bio = it }
                userEntity.gender?.let { gender = it }

                mainSkills.addAll(userEntity.mainSkills.map { it.name })
                otherSkills.addAll(userEntity.otherSkills.map { it.name })
                spokenLanguages.addAll(userEntity.spokenLanguages.map { it.name })

                userEntity.cv?.let { cv = it }
                userEntity.portfolio?.let { portfolio = it }
                userEntity.profilePhoto?.let { profilePhoto = it }

                webAndSocialUrls = websiteAndSocialMediaUrls
            }
        }

        fun buildUserEntityFromUserDto(user: User, userEntity: UserEntity) {
            user.firstName.whenNotEmpty { userEntity.firstName = it }
            user.lastName.whenNotEmpty { userEntity.lastName = it }
            user.email.whenNotEmpty { userEntity.email = it }
            user.country.whenNotEmpty { userEntity.country = it }
            user.phone.whenNotEmpty { userEntity.phone = it }
            user.city.whenNotEmpty { userEntity.city = it }
            user.bio.whenNotEmpty { userEntity.bio = it }
            userEntity.gender = user.gender
            user.cv.whenNotEmpty { userEntity.cv = it }
            user.portfolio.whenNotEmpty { userEntity.portfolio = it }
            user.profilePhoto.whenNotEmpty { userEntity.profilePhoto = it }
            user.webAndSocialUrls.website.whenNotEmpty { userEntity.website = it }
            user.webAndSocialUrls.linkedin.whenNotEmpty { userEntity.linkedin = it }
            user.webAndSocialUrls.facebook.whenNotEmpty { userEntity.facebook = it }
            user.webAndSocialUrls.twitter.whenNotEmpty { userEntity.twitter = it }
            user.webAndSocialUrls.skype.whenNotEmpty { userEntity.skype = it }
            user.webAndSocialUrls.github.whenNotEmpty { userEntity.github = it }
        }
    }
}