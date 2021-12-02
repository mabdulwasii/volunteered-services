package org.volunteered.apps.service.impl

import io.mockk.clearMocks
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.volunteered.apps.repository.BenefitRepository
import org.volunteered.apps.repository.OrganizationRepository
import org.volunteered.apps.repository.OrganizationSubsidiaryRepository
import org.volunteered.libs.user.v1.UserServiceGrpcKt

@SpringBootTest
internal class OrganizationServiceImplTest {

    private val organizationRepository = mockk<OrganizationRepository>()
    private val organizationSubsidiaryRepository = mockk<OrganizationSubsidiaryRepository>()
    private val benefitRepository = mockk<BenefitRepository>()
    private val userServiceStub = mockk<UserServiceGrpcKt.UserServiceCoroutineStub>()
    private val service = OrganizationServiceImpl(
        organizationRepository,
        organizationSubsidiaryRepository,
        benefitRepository,
        userServiceStub)

    @BeforeEach
    fun setUp() {
        clearMocks(organizationRepository, organizationSubsidiaryRepository, benefitRepository, userServiceStub )
    }

    @Test
    fun `should` () : Unit = kotlin.run {

    }
}