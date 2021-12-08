package org.volunteered.apps.service.impl

import com.google.protobuf.Empty
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
import org.springframework.data.repository.findByIdOrNull
import org.volunteered.apps.entity.OrganizationEntity
import org.volunteered.apps.entity.OrganizationSubsidiaryEntity
import org.volunteered.apps.exception.CreatorDoesNotExistException
import org.volunteered.apps.exception.OrganizationAlreadyExistsException
import org.volunteered.apps.exception.OrganizationDoesNotExistException
import org.volunteered.apps.repository.BenefitRepository
import org.volunteered.apps.repository.OrganizationRepository
import org.volunteered.apps.repository.OrganizationSubsidiaryRepository
import org.volunteered.libs.organization.v1.*
import org.volunteered.libs.proto.common.v1.organizationSubsidiary
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
    }

    @Test
    fun `should throw exception if organization already exists` () : Unit = runBlocking {
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
        } returns boolValue { value = true }
        every { organizationRepository.existsByEmail(request.email) } returns true

        assertThrows<OrganizationAlreadyExistsException> { service.createOrganization(request) }
    }

    @Test
    fun `should create organization` () : Unit = runBlocking {
        val request = createOrganizationRequest {
            creatorId = DEFAULT_ID
            name = DEFAULT_ORG_NAME
            email = DEFAULT_EMAIL
            bio = BIO
            city = DEFAULT_CITY
            country = DEFAULT_COUNTRY
        }
        val organizationEntity = OrganizationEntity(
            id = DEFAULT_ID,
            name = request.name,
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

    @Test
    fun `should create organization subsidiary` () : Unit = runBlocking {
        val request = createOrganizationSubsidiaryRequest {
            creatorId = DEFAULT_ID
            organizationId = DEFAULT_SUBSIDIARY_ID
            subsidiary = organizationSubsidiary {
                name = DEFAULT_SUBSIDIARY_NAME
                email = DEFAULT_SUBSIDIARY_EMAIL
                city =  DEFAULT_SUBSIDIARY_CITY
                country = DEFAULT_SUBSIDIARY_COUNTRY
                description = DEFAULT_SUBSIDIARY_DESCRIPTION
                phone = DEFAULT_SUBSIDIARY_PHONE
            }
        }
        val organizationEntity = OrganizationEntity(
            id =  request.creatorId,
            name = DEFAULT_ORG_NAME,
            email = DEFAULT_EMAIL,
            bio = BIO
        )
        val organizationSubsidiaryEntity = OrganizationSubsidiaryEntity(
            id = DEFAULT_SUBSIDIARY_ID,
            name = DEFAULT_SUBSIDIARY_NAME,
            email = DEFAULT_SUBSIDIARY_EMAIL,
            city = DEFAULT_SUBSIDIARY_CITY,
            phone = DEFAULT_SUBSIDIARY_PHONE,
            country = DEFAULT_SUBSIDIARY_COUNTRY,
            description = DEFAULT_SUBSIDIARY_DESCRIPTION,
            parent = organizationEntity
        )

        every {
            runBlocking {  userServiceStub.existsById(existsByIdRequest { id = request.creatorId }) }
        } returns boolValue { value = true }
        every { organizationRepository.findByIdOrNull(request.organizationId) } returns organizationEntity
        every { organizationSubsidiaryRepository.save(any()) } returns organizationSubsidiaryEntity

        val createdOrganizationSubsidiary = service.createOrganizationSubsidiary(request)

        assertNotNull(createdOrganizationSubsidiary)
        assertEquals(request.subsidiary.name, createdOrganizationSubsidiary.name)
        assertEquals(request.subsidiary.email, createdOrganizationSubsidiary.email)
        assertEquals(request.subsidiary.phone, createdOrganizationSubsidiary.phone)
        assertEquals(request.subsidiary.country, createdOrganizationSubsidiary.country)
        assertEquals(request.subsidiary.city, createdOrganizationSubsidiary.city)
        assertEquals(request.subsidiary.description, createdOrganizationSubsidiary.description)
        assertNotNull(createdOrganizationSubsidiary.id)
    }

    @Test
    fun `should not create organization subsidiary if creator does not exists` () : Unit = runBlocking {
        val request = createOrganizationSubsidiaryRequest {
            creatorId = DEFAULT_ID
            organizationId = DEFAULT_SUBSIDIARY_ID
            subsidiary = organizationSubsidiary {
                name = DEFAULT_SUBSIDIARY_NAME
                email = DEFAULT_SUBSIDIARY_EMAIL
                city =  DEFAULT_SUBSIDIARY_CITY
                country = DEFAULT_SUBSIDIARY_COUNTRY
                description = DEFAULT_SUBSIDIARY_DESCRIPTION
                phone = DEFAULT_SUBSIDIARY_PHONE
            }
        }

        every {
            runBlocking {  userServiceStub.existsById(existsByIdRequest { id = request.creatorId }) }
        } returns boolValue { value = false }

        assertThrows<CreatorDoesNotExistException> { service.createOrganizationSubsidiary(request)}

    }

    @Test
    fun `should not create organization subsidiary if organizationEntity does not exist` () : Unit = runBlocking {
        val request = createOrganizationSubsidiaryRequest {
            creatorId = DEFAULT_ID
            organizationId = DEFAULT_SUBSIDIARY_ID
            subsidiary = organizationSubsidiary {
                name = DEFAULT_SUBSIDIARY_NAME
                email = DEFAULT_SUBSIDIARY_EMAIL
                city =  DEFAULT_SUBSIDIARY_CITY
                country = DEFAULT_SUBSIDIARY_COUNTRY
                description = DEFAULT_SUBSIDIARY_DESCRIPTION
                phone = DEFAULT_SUBSIDIARY_PHONE
            }
        }

        every {
            runBlocking {  userServiceStub.existsById(existsByIdRequest { id = request.creatorId }) }
        } returns boolValue { value = true }

        every { organizationRepository.findByIdOrNull(request.organizationId) } returns null

        assertThrows<OrganizationDoesNotExistException> { service.createOrganizationSubsidiary(request)}
    }

    @Test
    fun `should update organization`() : Unit = runBlocking{
        val request = updateOrganizationRequest {
            id = DEFAULT_ID
            hqId = DEFAULT_SUBSIDIARY_ID
            name = DEFAULT_ORG_NAME
            bio = BIO
            logo = "logo_url"
            phone = DEFAULT_ORG_PHONE
            numberOfEmployees = 21
        }
        val organizationEntity = OrganizationEntity(
            id = DEFAULT_ID,
            name = DEFAULT_ORG_NAME,
            email = DEFAULT_EMAIL,
            bio = BIO
        )
        val organizationHqEntity = OrganizationSubsidiaryEntity(
            id = DEFAULT_SUBSIDIARY_ID,
            name = DEFAULT_SUBSIDIARY_NAME,
            email = DEFAULT_SUBSIDIARY_EMAIL,
            city = DEFAULT_SUBSIDIARY_CITY,
            country = DEFAULT_SUBSIDIARY_COUNTRY,
            description = DEFAULT_SUBSIDIARY_DESCRIPTION,
            parent = organizationEntity
        )

        every { organizationRepository.findByIdOrNull(request.id) } returns organizationEntity
        every { organizationSubsidiaryRepository.findByIdOrNull(request.hqId) } returns organizationHqEntity
        every { organizationRepository.save(organizationEntity) } returns organizationEntity

        val updatedOrganization = service.updateOrganization(request)

        assertNotNull(updatedOrganization)
        assertEquals(request.logo, updatedOrganization.logo)
        assertEquals(request.phone, updatedOrganization.phone)
        assertEquals(request.numberOfEmployees, updatedOrganization.numberOfEmployees)
        assertEquals(request.hqId, updatedOrganization.hq.id)
    }

    @Test
    fun `should not update organization if organizationEntity does not exist`() : Unit = runBlocking{
        val request = updateOrganizationRequest {
            id = DEFAULT_ID
            hqId = DEFAULT_SUBSIDIARY_ID
            name = DEFAULT_ORG_NAME
            bio = BIO
            logo = "logo_url"
            phone = DEFAULT_ORG_PHONE
            numberOfEmployees = 21
        }

        every { organizationRepository.findByIdOrNull(request.id) } returns null

        assertThrows<OrganizationDoesNotExistException> { service.updateOrganization(request)}
    }

    @Test
    fun `should not update organization if organization hq does not exist`() : Unit = runBlocking{
        val request = updateOrganizationRequest {
            id = DEFAULT_ID
            hqId = DEFAULT_SUBSIDIARY_ID
            name = DEFAULT_ORG_NAME
            bio = BIO
            logo = "logo_url"
            phone = DEFAULT_ORG_PHONE
            numberOfEmployees = 21
        }
        val organizationEntity = OrganizationEntity(
            id = DEFAULT_ID,
            name = DEFAULT_ORG_NAME,
            email = DEFAULT_EMAIL,
            bio = BIO
        )

        every { organizationRepository.findByIdOrNull(request.id) } returns organizationEntity
        every { organizationSubsidiaryRepository.findByIdOrNull(request.hqId) } returns null

        assertThrows<OrganizationDoesNotExistException> { service.updateOrganization(request)}
    }

    @Test
    fun `should delete organization`() : Unit = runBlocking {
        val request = deleteOrganizationRequest {
            id = DEFAULT_ID
        }

        every { organizationRepository.deleteById(request.id) } returns Unit

        val deletedOrganization = service.deleteOrganization(request)

        assertNotNull(deletedOrganization)
        assertEquals(deletedOrganization, Empty.getDefaultInstance())
    }

    @Test
    fun `should delete organization subsidiary`() : Unit = runBlocking {
        val request = deleteOrganizationSubsidiaryRequest {
            id = DEFAULT_ID
        }

        every { organizationSubsidiaryRepository.deleteById(request.id) } returns Unit

        val deletedSubsidiaryOrganization = service.deleteOrganizationSubsidiary(request)

        assertNotNull(deletedSubsidiaryOrganization)
        assertEquals(deletedSubsidiaryOrganization, Empty.getDefaultInstance())
    }

    @Test
    fun `should get organization subsidiary by Id`() : Unit = runBlocking {
        val request = getOrganizationSubsidiaryRequest{
            id = DEFAULT_SUBSIDIARY_ID
        }
        val  organizationSubsidiaryEntity = OrganizationSubsidiaryEntity(
            id = DEFAULT_SUBSIDIARY_ID,
            name = DEFAULT_SUBSIDIARY_NAME,
            email = DEFAULT_SUBSIDIARY_EMAIL,
            city = DEFAULT_SUBSIDIARY_CITY,
            country = DEFAULT_SUBSIDIARY_COUNTRY,
            phone = DEFAULT_SUBSIDIARY_PHONE,
            description = DEFAULT_SUBSIDIARY_DESCRIPTION,
            parent = OrganizationEntity(
                id =  DEFAULT_ID,
                name = DEFAULT_ORG_NAME,
                email = DEFAULT_EMAIL,
                bio =  BIO
            )
        )

        every { organizationSubsidiaryRepository.findByIdOrNull(request.id) } returns organizationSubsidiaryEntity

        val retrievedOrganizationSubsidiary = service.getOrganizationSubsidiaryById(request)

        assertNotNull(retrievedOrganizationSubsidiary)
        assertEquals(request.id, retrievedOrganizationSubsidiary.id)
        assertEquals(organizationSubsidiaryEntity.city, retrievedOrganizationSubsidiary.city)
        assertEquals(organizationSubsidiaryEntity.country, retrievedOrganizationSubsidiary.country)
        assertEquals(organizationSubsidiaryEntity.email, retrievedOrganizationSubsidiary.email)
        assertEquals(organizationSubsidiaryEntity.phone, retrievedOrganizationSubsidiary.phone)
        assertEquals(organizationSubsidiaryEntity.description, retrievedOrganizationSubsidiary.description)
    }

    @Test
    fun `should get organization by Id`() : Unit = runBlocking {
        val request = getOrganizationRequest{
            id = DEFAULT_ID
        }
        val  organizationEntity = OrganizationEntity(
            id = request.id,
            name = DEFAULT_ORG_NAME,
            email = DEFAULT_EMAIL,
            phone = DEFAULT_ORG_PHONE,
            bio =  BIO
        )

        every { organizationRepository.findByIdOrNull(request.id) } returns organizationEntity

        val retrievedOrganization = service.getOrganizationById(request)

        assertNotNull(retrievedOrganization)
        assertEquals(request.id, retrievedOrganization.id)
        assertEquals(organizationEntity.bio, retrievedOrganization.bio)
        assertEquals(organizationEntity.name, retrievedOrganization.name)
        assertEquals(organizationEntity.email, retrievedOrganization.email)
        assertEquals(organizationEntity.phone, retrievedOrganization.phone)
    }

    @Test
    fun `should search organization by Name`() : Unit = runBlocking {
        val request = getOrganizationByNameRequest{
            name = DEFAULT_ORG_NAME
        }
        val  organizationEntity = OrganizationEntity(
            id = DEFAULT_ID,
            name = request.name,
            email = DEFAULT_EMAIL,
            phone = DEFAULT_ORG_PHONE,
            bio =  BIO
        )

        every { organizationRepository.findByName(request.name) } returns organizationEntity

        val retrievedOrganization = service.getOrganizationByName(request)

        assertNotNull(retrievedOrganization)
        assertEquals(request.name, retrievedOrganization.name)
        assertEquals(organizationEntity.bio, retrievedOrganization.bio)
        assertEquals(organizationEntity.name, retrievedOrganization.name)
        assertEquals(organizationEntity.email, retrievedOrganization.email)
        assertEquals(organizationEntity.phone, retrievedOrganization.phone)
    }

    @Test
    fun `should not get organization by name if name is invalid`() : Unit = runBlocking {
        val request = getOrganizationByNameRequest{
            name = INVALID_ORG_NAME
        }

        every { organizationRepository.findByName(request.name) } returns null

        assertThrows<OrganizationDoesNotExistException> { service.getOrganizationByName(request)}
    }

    @Test
    fun `should not get organization by Id if Id is invalid`() : Unit = runBlocking {
        val request = getOrganizationRequest{
            id = INVALID_ID
        }

        every { organizationRepository.findByIdOrNull(request.id) } returns null

        assertThrows<OrganizationDoesNotExistException> { service.getOrganizationById(request)}
    }

    @Test
    fun `should not get organization subsidiary by Id if Id is Invalid`() : Unit = runBlocking {
        val request = getOrganizationSubsidiaryRequest{
            id = DEFAULT_SUBSIDIARY_ID
        }

        every { organizationSubsidiaryRepository.findByIdOrNull(request.id) } returns null

        assertThrows<OrganizationDoesNotExistException> { service.getOrganizationSubsidiaryById(request)}
    }


    @Test
    fun `should update organization subsidiary`() : Unit = runBlocking {
        val request = organizationSubsidiary {
            id = DEFAULT_SUBSIDIARY_ID
            name = DEFAULT_SUBSIDIARY_NAME
            email = DEFAULT_SUBSIDIARY_EMAIL
            city = DEFAULT_SUBSIDIARY_CITY
            country = DEFAULT_SUBSIDIARY_COUNTRY
            phone = DEFAULT_SUBSIDIARY_PHONE
            description = DEFAULT_SUBSIDIARY_DESCRIPTION
        }
        val organizationSubsidiaryEntity = OrganizationSubsidiaryEntity(
            id =  request.id,
            name = request.name,
            email = request.email,
            city = request.city,
            country =  request.country,
            phone = request.phone,
            description = request.description,
            parent = OrganizationEntity(
                id = DEFAULT_ID,
                name = DEFAULT_ORG_NAME,
                email = DEFAULT_EMAIL,
                bio =  BIO
            )
        )

        every { organizationSubsidiaryRepository.findByIdOrNull(request.id) } returns organizationSubsidiaryEntity
        every { organizationSubsidiaryRepository.save(any()) } returns organizationSubsidiaryEntity

        val updatedOrganizationSubsidiary = service.updateOrganizationSubsidiary(request)

        assertNotNull(updatedOrganizationSubsidiary)
        assertEquals(request.id, updatedOrganizationSubsidiary.id)
        assertEquals(request.name, updatedOrganizationSubsidiary.name)
        assertEquals(request.email, updatedOrganizationSubsidiary.email)
        assertEquals(request.description, updatedOrganizationSubsidiary.description)
        assertEquals(request.city, updatedOrganizationSubsidiary.city)
        assertEquals(request.country, updatedOrganizationSubsidiary.country)
        assertEquals(request.phone, updatedOrganizationSubsidiary.phone)
    }

    @Test
    fun `should not update organization subsidiary if organization subsidiary does not exist`() : Unit = runBlocking {
        val request = organizationSubsidiary {
            id = DEFAULT_SUBSIDIARY_ID
            name = DEFAULT_SUBSIDIARY_NAME
            email = DEFAULT_SUBSIDIARY_EMAIL
            city = DEFAULT_SUBSIDIARY_CITY
            country = DEFAULT_SUBSIDIARY_COUNTRY
            phone = DEFAULT_SUBSIDIARY_PHONE
            description = DEFAULT_SUBSIDIARY_DESCRIPTION
        }

        every { organizationSubsidiaryRepository.findByIdOrNull(request.id) } returns null

        assertThrows<OrganizationDoesNotExistException> { service.updateOrganizationSubsidiary(request)}
    }

    companion object {
        const val DEFAULT_ORG_NAME = "Wright Enterprise"
        const val INVALID_ORG_NAME = "Invalid name"
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
        const val INVALID_ID = 222L
        const val DEFAULT_SUBSIDIARY_ID = 21L
        const val DEFAULT_WEBSITE = "website url"
        const val DEFAULT_LINKEDIN = "linkedin url"
        const val BIO = "my bio"
        const val EMPTY_STRING = ""
    }
}