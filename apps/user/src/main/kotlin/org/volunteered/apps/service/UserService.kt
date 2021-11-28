package org.volunteered.apps.service

import com.google.protobuf.Empty
import org.volunteered.libs.common.v1.User
import org.volunteered.libs.user.v1.CreateUserRequest
import org.volunteered.libs.user.v1.DeleteUserRequest
import org.volunteered.libs.user.v1.GetUserByEmailRequest
import org.volunteered.libs.user.v1.GetUserByIdRequest

interface UserService {
    fun createUser(request: CreateUserRequest): User
    fun getUserById(request: GetUserByIdRequest): User
    fun getUserByEmail(request: GetUserByEmailRequest): User
    fun updateUser(request: User): User
    fun deleteUser(request: DeleteUserRequest): Empty
}
