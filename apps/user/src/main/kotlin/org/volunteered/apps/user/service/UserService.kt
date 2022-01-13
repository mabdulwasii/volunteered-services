package org.volunteered.apps.user.service

import com.google.protobuf.BoolValue
import com.google.protobuf.Empty
import org.volunteered.libs.proto.common.v1.Id
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.user.v1.CreateUserRequest
import org.volunteered.libs.proto.user.v1.DeleteUserRequest
import org.volunteered.libs.proto.user.v1.GetUserByEmailRequest
import org.volunteered.libs.proto.user.v1.GetUserByIdRequest

interface UserService {
    suspend fun createUser(request: CreateUserRequest): User
    suspend fun existsById(request: Id): BoolValue
    suspend fun getUserById(request: GetUserByIdRequest): User
    suspend fun getUserByEmail(request: GetUserByEmailRequest): User
    suspend fun updateUser(request: User): User
    suspend fun deleteUser(request: DeleteUserRequest): Empty
}
