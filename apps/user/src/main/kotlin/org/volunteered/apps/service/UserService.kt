package org.volunteered.apps.service

import org.volunteered.libs.common.v1.User
import org.volunteered.libs.common.v1.user
import org.volunteered.libs.user.v1.CreateUserRequest
import org.volunteered.libs.user.v1.UpdateUserRequest
import org.volunteered.libs.user.v1.UserServiceGrpcKt

class UserService: UserServiceGrpcKt.UserServiceCoroutineImplBase() {
    override suspend fun createUser(request: CreateUserRequest): User {
        return user {
            firstName = "Femi"
            lastName = "Shobande"
            phone = "1234"
            email = "femi@gmail.com"
        }
    }

    override suspend fun update(request: UpdateUserRequest): User {
        return user {
            firstName = "Tunji"
            lastName = "Moronkola"
            phone = "1234"
            email = "tunji@gmail.com"
        }
    }
}