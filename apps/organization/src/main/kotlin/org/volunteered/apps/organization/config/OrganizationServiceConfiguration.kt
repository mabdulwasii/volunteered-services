package org.volunteered.apps.organization.config

import net.devh.boot.grpc.client.inject.GrpcClient
import net.devh.boot.grpc.client.inject.GrpcClientBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.volunteered.apps.organization.repository.BenefitRepository
import org.volunteered.apps.organization.repository.OrganizationRepository
import org.volunteered.apps.organization.repository.OrganizationSubsidiaryRepository
import org.volunteered.apps.organization.service.OrganizationService
import org.volunteered.apps.organization.service.impl.OrganizationServiceImpl
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@GrpcClientBean(
    clazz = UserServiceGrpcKt.UserServiceCoroutineStub::class,
    beanName = "userServiceCoroutineStub",
    client = GrpcClient("user-service")
)
class OrganizationServiceConfiguration {
    @Bean
    fun organizationService(
        @Autowired organizationRepository: OrganizationRepository,
        @Autowired organizationSubsidiaryRepository: OrganizationSubsidiaryRepository,
        @Autowired benefitRepository: BenefitRepository,
        @Autowired userServiceCoroutineStub: UserServiceGrpcKt.UserServiceCoroutineStub
    ): OrganizationService {
        return OrganizationServiceImpl(
            organizationRepository,
            organizationSubsidiaryRepository,
            benefitRepository,
            userServiceCoroutineStub)
    }
}
