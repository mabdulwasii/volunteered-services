package org.volunteered.apps.grpc

import com.google.protobuf.BoolValue
import com.google.protobuf.Empty
import net.devh.boot.grpc.server.service.GrpcService
import org.volunteered.apps.service.UserService
import org.volunteered.libs.core.exception.InvalidCountryCodeException
import org.volunteered.libs.core.extension.whenNotEmpty
import org.volunteered.libs.core.util.IsoUtil
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.user.v1.CreateUserRequest
import org.volunteered.libs.proto.user.v1.DeleteUserRequest
import org.volunteered.libs.proto.user.v1.ExistsByIdRequest
import org.volunteered.libs.proto.user.v1.GetUserByEmailRequest
import org.volunteered.libs.proto.user.v1.GetUserByIdRequest
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt

@GrpcService
class UserGrpcService(private val userService: UserService) : UserServiceGrpcKt.UserServiceCoroutineImplBase() {
    override suspend fun createUser(request: CreateUserRequest): User {
        if (!IsoUtil.isValidISOCountry(request.country))
            throw InvalidCountryCodeException("Invalid country code ${request.country}")

        return userService.createUser(request)
    }

    override suspend fun existsById(request: ExistsByIdRequest): BoolValue {
        return userService.existsById(request)
    }

    override suspend fun getUserById(request: GetUserByIdRequest): User {
        return userService.getUserById(request)
    }

    override suspend fun getUserByEmail(request: GetUserByEmailRequest): User {
        return userService.getUserByEmail(request)
    }

    override suspend fun updateUser(request: User): User {
        request.country.whenNotEmpty {
            if (!IsoUtil.isValidISOCountry(request.country))
                throw InvalidCountryCodeException("Invalid country code ${request.country}")
        }

        return userService.updateUser(request)
    }

    override suspend fun deleteUser(request: DeleteUserRequest): Empty {
        return userService.deleteUser(request)
    }
}