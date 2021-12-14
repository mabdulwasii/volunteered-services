package org.volunteered.apps.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.volunteered.apps.entity.Benefit
import org.volunteered.apps.entity.OrganizationEntity
import org.volunteered.apps.entity.OrganizationSubsidiaryEntity
import org.volunteered.libs.proto.common.v1.organizationSubsidiary
import org.volunteered.libs.proto.common.v1.websiteAndSocialMediaUrls
import org.volunteered.libs.proto.organization.v1.createOrganizationRequest
import org.volunteered.libs.proto.organization.v1.createOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.updateOrganizationRequest
import org.volunteered.libs.proto.organization.v1.updateOrganizationSubsidiaryRequest

internal class DtoTransformerTest {
    @Test
    fun `should transform createOrganizationRequest to organizationEntity`() {
        val request = createOrganizationRequest {
            name = DEFAULT_ORG_NAME
            email = DEFAULT_EMAIL
            bio = BIO
        }

        val transFormedOrgEntity =
            DtoTransformer.transformCreateOrganizationRequestToOrganizationEntity(request)

        assertEquals(DEFAULT_ORG_NAME, transFormedOrgEntity.name)
        assertEquals(DEFAULT_EMAIL, transFormedOrgEntity.email)
        assertEquals(BIO, transFormedOrgEntity.bio)
    }

    @Test
    fun `should transform createOrganizationSubsidiaryRequest to organizationSubsidiaryEntity`() {
        val organizationEntity = OrganizationEntity(
            id = DEFAULT_ID,
            name = DEFAULT_ORG_NAME,
            email = DEFAULT_EMAIL,
            bio = BIO,
        )
        val request = createOrganizationSubsidiaryRequest {
            subsidiary = organizationSubsidiary {
                name = DEFAULT_SUBSIDIARY_NAME
                email = DEFAULT_SUBSIDIARY_EMAIL
                city = DEFAULT_SUBSIDIARY_CITY
                country = DEFAULT_SUBSIDIARY_COUNTRY
                phone = DEFAULT_SUBSIDIARY_PHONE
                description = DEFAULT_SUBSIDIARY_DESCRIPTION
            }
        }

        val transformedOrganizationEntity =
            DtoTransformer.transformCreateOrganizationSubsidiaryRequestToOrganizationSubsidiaryEntity(
                request,
                organizationEntity
            )

        assertEquals(organizationEntity, transformedOrganizationEntity.parent)
        assertEquals(organizationEntity.id, transformedOrganizationEntity.parent.id)
        assertEquals(organizationEntity.name, transformedOrganizationEntity.parent.name)
        assertEquals(organizationEntity.email, transformedOrganizationEntity.parent.email)
        assertEquals(organizationEntity.bio, transformedOrganizationEntity.parent.bio)
        assertEquals(DEFAULT_SUBSIDIARY_NAME, transformedOrganizationEntity.name)
        assertEquals(DEFAULT_SUBSIDIARY_EMAIL, transformedOrganizationEntity.email)
        assertEquals(DEFAULT_SUBSIDIARY_CITY, transformedOrganizationEntity.city)
        assertEquals(DEFAULT_SUBSIDIARY_COUNTRY, transformedOrganizationEntity.country)
        assertEquals(DEFAULT_SUBSIDIARY_PHONE, transformedOrganizationEntity.phone)
        assertEquals(DEFAULT_SUBSIDIARY_DESCRIPTION, transformedOrganizationEntity.description)
    }

    @Test
    fun `should transform organizationEntity to OrganizationDto` () {
        val organizationEntity = OrganizationEntity(
            id = DEFAULT_ID,
            name = DEFAULT_ORG_NAME,
            email = DEFAULT_EMAIL,
            bio = BIO,
            phone = DEFAULT_ORG_PHONE,
            website = DEFAULT_WEBSITE,
            linkedin = DEFAULT_LINKEDIN,
            founded = 1990,
            numberOfEmployees = 230,
            benefits = setOf(Benefit(1, "Benefit 1"), Benefit(2, "Benefit 2"))
        )
        val subsidiary = OrganizationSubsidiaryEntity (
            name = DEFAULT_SUBSIDIARY_NAME,
            email = DEFAULT_SUBSIDIARY_EMAIL,
            city = DEFAULT_SUBSIDIARY_CITY,
            country = DEFAULT_SUBSIDIARY_COUNTRY,
            phone = DEFAULT_SUBSIDIARY_PHONE,
            description = DEFAULT_SUBSIDIARY_DESCRIPTION,
            parent = organizationEntity
        )
        organizationEntity.hq = subsidiary

        val transformedOrganizationDto =
            DtoTransformer.transformOrganizationEntityToOrganizationDto(organizationEntity)

        assertNotNull(transformedOrganizationDto.id)
        assertNotNull(transformedOrganizationDto)
        assertEquals(DEFAULT_ID, transformedOrganizationDto.id)
        assertEquals(DEFAULT_ORG_NAME, transformedOrganizationDto.name)
        assertEquals(DEFAULT_EMAIL, transformedOrganizationDto.email)
        assertEquals(BIO, transformedOrganizationDto.bio)
        assertEquals(DEFAULT_ORG_PHONE, transformedOrganizationDto.phone)
        assertEquals(DEFAULT_LINKEDIN, transformedOrganizationDto.webAndSocialUrls.linkedin)
        assertEquals(DEFAULT_WEBSITE, transformedOrganizationDto.webAndSocialUrls.website)
        assertEquals(DEFAULT_SUBSIDIARY_NAME, transformedOrganizationDto.hq.name)
        assertEquals(DEFAULT_SUBSIDIARY_EMAIL, transformedOrganizationDto.hq.email)
        assertEquals(DEFAULT_SUBSIDIARY_CITY, transformedOrganizationDto.hq.city)
        assertEquals(DEFAULT_SUBSIDIARY_COUNTRY, transformedOrganizationDto.hq.country)
        assertEquals(DEFAULT_SUBSIDIARY_PHONE, transformedOrganizationDto.hq.phone)
        assertEquals(DEFAULT_SUBSIDIARY_DESCRIPTION, transformedOrganizationDto.hq.description)
    }

    @Test
    fun `should throw exception if id is null when transform organizationEntity to OrganizationDto` () {
        val organizationEntity = OrganizationEntity(
            name = DEFAULT_ORG_NAME,
            email = DEFAULT_EMAIL,
            bio = BIO,
            phone = DEFAULT_ORG_PHONE,
            website = DEFAULT_WEBSITE,
            linkedin = DEFAULT_LINKEDIN,
            founded = 1990,
            numberOfEmployees = 230,
            benefits = setOf(Benefit(1, "Benefit 1"), Benefit(2, "Benefit 2"))
        )
        val subsidiary = OrganizationSubsidiaryEntity (
            name = DEFAULT_SUBSIDIARY_NAME,
            email = DEFAULT_SUBSIDIARY_EMAIL,
            city = DEFAULT_SUBSIDIARY_CITY,
            country = DEFAULT_SUBSIDIARY_COUNTRY,
            phone = DEFAULT_SUBSIDIARY_PHONE,
            description = DEFAULT_SUBSIDIARY_DESCRIPTION,
            parent = organizationEntity
        )
        organizationEntity.hq = subsidiary

        assertThrows(NullPointerException::class.java) {
            DtoTransformer.transformOrganizationEntityToOrganizationDto(organizationEntity)
        }
    }

    @Test
    fun `should transform organizationSubsidiaryEntity to organizationSubsidiaryDto` () {
        val organizationSubsidiaryEntity = OrganizationSubsidiaryEntity(
            id = DEFAULT_SUBSIDIARY_ID,
            name = DEFAULT_SUBSIDIARY_NAME,
            email = DEFAULT_SUBSIDIARY_EMAIL,
            city = DEFAULT_SUBSIDIARY_CITY,
            country = DEFAULT_SUBSIDIARY_COUNTRY,
            phone = DEFAULT_SUBSIDIARY_PHONE,
            description = DEFAULT_SUBSIDIARY_DESCRIPTION,
            parent = OrganizationEntity(
                name = DEFAULT_ORG_NAME,
                email = DEFAULT_EMAIL,
                bio = BIO
            )
        )

        val transformedOrganizationSubsidiaryEntity =
            DtoTransformer.transformOrganizationSubsidiaryEntityToOrganizationSubsidiaryDto(organizationSubsidiaryEntity)

        assertNotNull(transformedOrganizationSubsidiaryEntity)
        assertNotNull(transformedOrganizationSubsidiaryEntity.id)
        assertEquals(DEFAULT_SUBSIDIARY_ID, transformedOrganizationSubsidiaryEntity.id)
        assertEquals(DEFAULT_SUBSIDIARY_NAME, transformedOrganizationSubsidiaryEntity.name)
        assertEquals(DEFAULT_SUBSIDIARY_EMAIL, transformedOrganizationSubsidiaryEntity.email)
        assertEquals(DEFAULT_SUBSIDIARY_CITY, transformedOrganizationSubsidiaryEntity.city)
        assertEquals(DEFAULT_SUBSIDIARY_COUNTRY, transformedOrganizationSubsidiaryEntity.country)
        assertEquals(DEFAULT_SUBSIDIARY_PHONE, transformedOrganizationSubsidiaryEntity.phone)
        assertEquals(DEFAULT_SUBSIDIARY_DESCRIPTION, transformedOrganizationSubsidiaryEntity.description)
        assertEquals(DEFAULT_SUBSIDIARY_DESCRIPTION, transformedOrganizationSubsidiaryEntity.description)
    }

    @Test
    fun `should build organizationEntity from organizationDto field not empty or null`() {
        val request = updateOrganizationRequest {
            id = DEFAULT_ID
            name = DEFAULT_ORG_NAME
            email = DEFAULT_EMAIL
            bio = BIO
            logo = EMPTY_STRING
            phone = EMPTY_STRING
            webAndSocialUrls = websiteAndSocialMediaUrls {
                linkedin = DEFAULT_LINKEDIN
                website = DEFAULT_WEBSITE
            }
            numberOfEmployees = 200
        }

        val organizationEntity = OrganizationEntity(
            id = DEFAULT_ID,
            name = DEFAULT_ORG_NAME,
            email = DEFAULT_EMAIL,
            bio = BIO,
            logo = "logo url",
            phone = DEFAULT_ORG_PHONE,
            linkedin = DEFAULT_LINKEDIN,
            website = DEFAULT_WEBSITE,
            numberOfEmployees = 201
        )

        DtoTransformer.buildOrganizationEntityFromOrganizationDto(request, organizationEntity)

        assertEquals(request.id, organizationEntity.id)
        assertEquals(request.name, organizationEntity.name)
        assertEquals(request.email, organizationEntity.email)
        assertEquals(request.bio, organizationEntity.bio)
        assertEquals(request.numberOfEmployees, organizationEntity.numberOfEmployees)
        assertEquals(request.webAndSocialUrls.website, organizationEntity.website)
        assertEquals(request.webAndSocialUrls.linkedin, organizationEntity.linkedin)
        assertNotEquals(request.logo, organizationEntity.logo)
        assertNotEquals(request.phone, organizationEntity.phone)
        assertEquals("logo url", organizationEntity.logo)
        assertEquals(DEFAULT_ORG_PHONE, organizationEntity.phone)
        assertEquals(200, organizationEntity.numberOfEmployees)
    }

    @Test
    fun `should build organizationSubsidiaryEntity from organizationSubsidiaryDto field not empty or null`() {
        val organizationSubsidiaryEntity = OrganizationSubsidiaryEntity(
            id = DEFAULT_SUBSIDIARY_ID,
            name = DEFAULT_SUBSIDIARY_NAME,
            email = DEFAULT_EMAIL,
            city = DEFAULT_SUBSIDIARY_CITY,
            country = DEFAULT_SUBSIDIARY_COUNTRY,
            phone = DEFAULT_SUBSIDIARY_PHONE,
            parent = OrganizationEntity(
                name = DEFAULT_ORG_NAME,
                email = DEFAULT_EMAIL,
                bio = BIO
            )
        )

        val updateOrganizationSubsidiaryRequest = updateOrganizationSubsidiaryRequest {
            name = EMPTY_STRING
            email = EMPTY_STRING
            city = "Abuja"
            country = "NG"
            phone = "+436363737"
            description = DEFAULT_SUBSIDIARY_DESCRIPTION
        }

        DtoTransformer.buildOrganizationSubsidiaryEntityFromOrganizationSubsidiaryDto(updateOrganizationSubsidiaryRequest, organizationSubsidiaryEntity)

        assertNotEquals(updateOrganizationSubsidiaryRequest.name, organizationSubsidiaryEntity.name)
        assertNotEquals(updateOrganizationSubsidiaryRequest.email, organizationSubsidiaryEntity.email)
        assertEquals(updateOrganizationSubsidiaryRequest.city, organizationSubsidiaryEntity.city)
        assertEquals(updateOrganizationSubsidiaryRequest.country, organizationSubsidiaryEntity.country)
        assertEquals(updateOrganizationSubsidiaryRequest.phone, organizationSubsidiaryEntity.phone)
        assertEquals(updateOrganizationSubsidiaryRequest.description, organizationSubsidiaryEntity.description)
    }
    companion object {
        const val DEFAULT_ORG_NAME = "Wright Enterprise"
        const val DEFAULT_SUBSIDIARY_NAME = "Wright Subsidiary"
        const val DEFAULT_EMAIL = "admin@wright.com"
        const val DEFAULT_SUBSIDIARY_EMAIL = "subsidiary@wright.com"
        const val DEFAULT_SUBSIDIARY_CITY = "Abuja"
        const val DEFAULT_SUBSIDIARY_COUNTRY = "NG"
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