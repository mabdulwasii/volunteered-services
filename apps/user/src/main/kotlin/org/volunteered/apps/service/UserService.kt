package org.volunteered.apps.service

import net.devh.boot.grpc.server.service.GrpcService
import org.volunteered.libs.common.v1.User
import org.volunteered.libs.user.v1.CreateUserRequest
import org.volunteered.libs.user.v1.UpdateUserRequest
import org.volunteered.libs.user.v1.UserServiceGrpcKt

@GrpcService
class UserService(private val userDetailService: UserDetailService ): UserServiceGrpcKt.UserServiceCoroutineImplBase() {

    override suspend fun createUser(request: CreateUserRequest): User {
        val email = request.email
        val userDetails = userDetailService.findByEmail(email)

        return User.newBuilder()
            .setEmail(userDetails?.email)
            .setFirstName(userDetails?.firstName)
            .setLastName(userDetails?.lastName)
            .setPhone(userDetails?.phone)
            .setEmail(userDetails?.email)
            .setCountry(userDetails?.country)
            .build()

    }

    override suspend fun update(request: UpdateUserRequest): User {

        val id = request.id
        val user = request.user;
        var userDetails = userDetailService.findByEmail(id.value)

        userDetails?.phone = user.phone
        userDetails?.email = user.email
        userDetails?.firstName = user.firstName
        userDetails?.lastName = user.lastName

        userDetails = userDetails?.let { userDetailService.save(it) }

        return User.newBuilder()
            .setEmail(userDetails?.email)
            .setFirstName(userDetails?.firstName)
            .setLastName(userDetails?.lastName)
            .setPhone(userDetails?.phone)
            .setEmail(userDetails?.email)
            .setCountry(userDetails?.country)
            .build()

    }
}