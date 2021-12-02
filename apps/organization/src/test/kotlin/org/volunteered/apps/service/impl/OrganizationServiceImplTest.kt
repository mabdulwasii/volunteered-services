package org.volunteered.apps.service.impl

import com.google.protobuf.boolValue
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.volunteered.apps.entity.OrganizationEntity
import org.volunteered.apps.entity.OrganizationSubsidiaryEntity
import org.volunteered.apps.exception.CreatorDoesNotExistException
import org.volunteered.apps.repository.BenefitRepository
import org.volunteered.apps.repository.OrganizationRepository
import org.volunteered.apps.repository.OrganizationSubsidiaryRepository
import org.volunteered.libs.organization.v1.createOrganizationRequest
import org.volunteered.libs.user.v1.UserServiceGrpcKt
import org.volunteered.libs.user.v1.existsByIdRequest

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
    fun `should not create organization if creator does not exist` () : Unit = runBlocking {
        val request = createOrganizationRequest {
            creatorId = DEFAULT_ID
            name = DEFAULT_ORG_NAME
            email = DEFAULT_EMAIL
            bio = BIO
            city = DEFAULT_CITY
            country = DEFAULT_COUNTRY
        }


        every {
            runBlocking {  userServiceStub.existsById(existsByIdRequest { id = DEFAULT_ID }) }
        } returns boolValue { value = false }

        assertThrows<CreatorDoesNotExistException> { service.createOrganization(request) }
//        val organizationCreated = service.createOrganization(request)

//        assertNotNull(organizationCreated)
//        assertEquals(DEFAULT_ORG_NAME, organizationCreated.name)
//        assertEquals(DEFAULT_EMAIL, organizationCreated.email)
//        assertEquals(BIO, organizationCreated.bio)
//        assertEquals(DEFAULT_CITY, organizationCreated.hq.city)
//        assertEquals(DEFAULT_COUNTRY, organizationCreated.hq.country)
    }

    @Test
    fun `should create organization if creator exist` () : Unit = runBlocking {
        val request = createOrganizationRequest {
            creatorId = DEFAULT_ID
            name = DEFAULT_ORG_NAME
            email = DEFAULT_EMAIL
            bio = BIO
            city = DEFAULT_CITY
            country = DEFAULT_COUNTRY
        }
        val organizationEntity = OrganizationEntity(
            name = request.email,
            email = request.email,
            bio = request.bio,
        )
        val organizationSubsidiaryEntity = OrganizationSubsidiaryEntity(
            id = DEFAULT_ID,
            city = request.city,
            country = request.country,
            parent = organizationEntity
        )
        organizationEntity.hq = organizationSubsidiaryEntity

        every {
            runBlocking {  userServiceStub.existsById(existsByIdRequest { id = request.creatorId }) }
        } returns boolValue { value = true }

        every { organizationRepository.existsByEmail(request.email) } returns false

        every { organizationRepository.save(any()) } returns organizationEntity
        every { organizationSubsidiaryRepository.save(any()) } returns organizationSubsidiaryEntity

        val organizationCreated = service.createOrganization(request)

        assertNotNull(organizationCreated)
        assertEquals(DEFAULT_ORG_NAME, organizationCreated.name)
        assertEquals(DEFAULT_EMAIL, organizationCreated.email)
        assertEquals(BIO, organizationCreated.bio)
        assertEquals(request.city, organizationCreated.hq.city)
        assertEquals(request.country, organizationCreated.hq.country)
    }


    companion object {
        const val DEFAULT_ORG_NAME = "Wright Enterprise"
        const val DEFAULT_SUBSIDIARY_NAME = "Wright Subsidiary"
        const val DEFAULT_EMAIL = "admin@wright.com"
        const val DEFAULT_SUBSIDIARY_EMAIL = "subsidiary@wright.com"
        const val DEFAULT_SUBSIDIARY_CITY = "Abuja"
        const val DEFAULT_CITY = "Lagos"
        const val DEFAULT_SUBSIDIARY_COUNTRY = "NG"
        const val DEFAULT_COUNTRY = "NG"
        const val DEFAULT_SUBSIDIARY_PHONE = "+23467848844"
        const val DEFAULT_ORG_PHONE = "+146784844744"
        const val DEFAULT_SUBSIDIARY_DESCRIPTION = "Subidiary"
        const val DEFAULT_ID = 1L
        const val DEFAULT_SUBSIDIARY_ID = 21L
        const val DEFAULT_WEBSITE = "website url"
        const val DEFAULT_LINKEDIN = "linkedin url"
        const val BIO = "my bio"
        const val EMPTY_STRING = ""
    }
}