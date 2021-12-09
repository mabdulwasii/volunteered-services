package org.volunteered.apps.config

import net.devh.boot.grpc.client.inject.GrpcClient
import net.devh.boot.grpc.client.inject.GrpcClientBean
import net.devh.boot.grpc.client.inject.GrpcClientBeans
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.volunteered.apps.service.ReviewService
import org.volunteered.apps.service.impl.ReviewServiceImpl
import org.volunteered.libs.proto.organization.v1.OrganizationServiceGrpcKt
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@GrpcClientBeans(
    value = [
        GrpcClientBean(
            clazz = UserServiceGrpcKt.UserServiceCoroutineStub::class,
            beanName = "userServiceCoroutineStub",
            client = GrpcClient("user-service")
        ),
        GrpcClientBean(
            clazz = OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub::class,
            beanName = "organizationServiceCoroutineStub",
            client = GrpcClient("organization-service")
        )
    ]
)
class ReviewServiceConfiguration {
    @Bean
    fun reviewService(
        @Autowired userServiceCoroutineStub: UserServiceGrpcKt.UserServiceCoroutineStub,
        @Autowired organizationServiceCoroutineStub: OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub
    ): ReviewService {
        return ReviewServiceImpl(userServiceCoroutineStub, organizationServiceCoroutineStub)
    }
}