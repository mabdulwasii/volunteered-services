package org.volunteered.apps.recommendation.config

import net.devh.boot.grpc.client.inject.GrpcClient
import net.devh.boot.grpc.client.inject.GrpcClientBean
import net.devh.boot.grpc.client.inject.GrpcClientBeans
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.volunteered.apps.recommendation.service.RecommendationService
import org.volunteered.apps.recommendation.service.impl.RecommendationServiceImpl
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
class RecommendationServiceConfiguration {
    @Bean
    fun recommendationService(
        @Autowired userServiceCoroutineStub: UserServiceGrpcKt.UserServiceCoroutineStub,
        @Autowired organizationServiceCoroutineStub: OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub,
    ): RecommendationService {
        return RecommendationServiceImpl(userServiceCoroutineStub, organizationServiceCoroutineStub)
    }
}
