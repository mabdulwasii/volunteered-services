package org.volunteered.apps.service

import net.devh.boot.grpc.server.service.GrpcService
import org.volunteered.apps.entity.Gender
import org.volunteered.libs.common.v1.User
import org.volunteered.libs.common.v1.user
import org.volunteered.libs.user.v1.CreateUserRequest
import org.volunteered.libs.user.v1.GetRequest
import org.volunteered.libs.user.v1.UpdateUserRequest
import org.volunteered.libs.user.v1.UserServiceGrpcKt

@GrpcService
class UserService(private val userDetailService: UserDetailService ): UserServiceGrpcKt.UserServiceCoroutineImplBase() {

    override suspend fun createUser(request: CreateUserRequest): User {
        val email = request.email
        val userDetails = userDetailService.findByEmail(email)

        return user {
            userDetails?.email;
            userDetails?.firstName;
            userDetails?.lastName;
            userDetails?.phone;
            userDetails?.email;
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
        }
    }

    override suspend fun update(request: UpdateUserRequest): User {

        val email = request.email
        val user = request.user;
        var userDetails = userDetailService.findByEmail(email)

        userDetails?.phone = user.phone
        userDetails?.email = user.email
        userDetails?.firstName = user.firstName
        userDetails?.lastName = user.lastName
        userDetails?.phone = user.phone
        userDetails?.email = user.email
        userDetails?.country = user.country
        userDetails?.city = user.city
        userDetails?.bio = user.bio
        userDetails?.gender = Gender.valueOf(user.gender.name)
        userDetails?.resumeUrl = user.resumeUrl
        userDetails?.portfolioUrl = user.portfolioUrl
        userDetails?.profilePhotoUrl = user.profilePhotoUrl

        userDetails = userDetails?.let { userDetailService.save(it) }

        return user {
            userDetails?.email;
            userDetails?.firstName;
            userDetails?.lastName;
            userDetails?.phone;
            userDetails?.email;
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

    override suspend fun get(request: GetRequest): User {
        val email = request.email
        val userDetails = userDetailService.findByEmail(email)

        return user {
            userDetails?.email;
            userDetails?.firstName;
            userDetails?.lastName;
            userDetails?.phone;
            userDetails?.email;
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