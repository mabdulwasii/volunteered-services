package org.volunteered.apps.service

import com.google.protobuf.StringValue
import org.volunteered.libs.common.v1.user
import org.volunteered.libs.user.v1.CreateRequest
import org.volunteered.libs.user.v1.GetRequest
import org.volunteered.libs.user.v1.GetResponse
import org.volunteered.libs.user.v1.UserServiceGrpcKt
import org.volunteered.libs.user.v1.getResponse

class UserService: UserServiceGrpcKt.UserServiceCoroutineImplBase() {
    override suspend fun get(request: GetRequest): GetResponse {
        return getResponse {
            user {
                firstName = "Femi"
                lastName = "Shobande"
                phone = "1234"
                email = "femi@gmail.com"
            }
        }
    }

    override suspend fun create(request: CreateRequest): StringValue {
        return StringValue.of("123e4567-e89b-12d3-a456-426614174000")
    }
}