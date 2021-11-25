package org.volunteered.apps.service

import com.google.protobuf.Empty
import net.devh.boot.grpc.server.service.GrpcService
import org.volunteered.apps.service.exception.UserAlreadyExistException
import org.volunteered.apps.service.exception.UserNotExistException
import org.volunteered.apps.utilities.Utils
import org.volunteered.libs.common.v1.User
import org.volunteered.libs.user.v1.*

@GrpcService
class UserGrpcService(
    private val userDetailsService: UserDetailsService,
    private val utils: Utils
) : UserServiceGrpcKt.UserServiceCoroutineImplBase() {

    override suspend fun createUser(request: CreateUserRequest): User {

        val email = request.email

        val existsByEmail = userDetailsService.existsByEmail(email)

        if (!existsByEmail) {

            val userDetails = utils.convertCreateUserRequestToUserDetails(request)

            val savedUserDetails = userDetailsService.save(userDetails)

            return utils.convertUserDetailsToUser(savedUserDetails)
        }

        throw UserAlreadyExistException("User already exists")
    }

    override suspend fun updateUser(request: UpdateUserRequest): User {

        val user = request.user
        val id = request.id

        var userDetails = userDetailsService.findById(id)

        if (userDetails != null) {

            val mainSkills = utils.convertStringToSkillSet(user.mainSkillsList.toList())

            val otherSkills = utils.convertStringToSkillSet(user.otherSkillsList.toList())

            val spokenLanguages = utils.convertStringToLanguageSet(user.spokenLanguagesList.toList())

            userDetails =
                utils.buildUserDetailsFromUserUpdateRequest(user, userDetails, mainSkills, otherSkills, spokenLanguages)

            val updatedUserDetails = userDetails?.let { userDetailsService.save(it) }

            return utils.convertUserDetailsToUser(updatedUserDetails)
        }

        throw UserNotExistException("Invalid user")
    }

    override suspend fun getUser(request: GetUserRequest): User {
        val id = request.id
        val userDetails = userDetailsService.findById(id)

        return utils.convertUserDetailsToUser(userDetails)
    }

    override suspend fun deleteUser(request: DeleteUserRequest): Empty {
        val id = request.id
        userDetailsService.deleteById(id)
        return Empty.getDefaultInstance()
    }
}