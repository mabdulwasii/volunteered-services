package org.volunteered.apps.util

import org.volunteered.apps.entity.OrganizationEntity
import org.volunteered.apps.entity.OrganizationSubsidiaryEntity
import org.volunteered.libs.core.extension.whenGreaterThanZero
import org.volunteered.libs.core.extension.whenNotEmpty
import org.volunteered.libs.proto.common.v1.Organization
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.common.v1.organization
import org.volunteered.libs.proto.common.v1.organizationSubsidiary
import org.volunteered.libs.proto.common.v1.websiteAndSocialMediaUrls
import org.volunteered.libs.proto.organization.v1.CreateOrganizationRequest
import org.volunteered.libs.proto.organization.v1.CreateOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.SearchOrganizationByNameResponse
import org.volunteered.libs.proto.organization.v1.UpdateOrganizationRequest
import org.volunteered.libs.proto.organization.v1.UpdateOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.searchOrganizationByNameResponse

class DtoTransformer {
    companion object {
        fun transformCreateOrganizationRequestToOrganizationEntity(request: CreateOrganizationRequest): OrganizationEntity {
            return OrganizationEntity(
                name = request.name,
                email = request.email,
                bio = request.bio
            )
        }

        fun transformCreateOrganizationSubsidiaryRequestToOrganizationSubsidiaryEntity(
            request: CreateOrganizationSubsidiaryRequest,
            organizationEntity: OrganizationEntity
        ): OrganizationSubsidiaryEntity {
            return OrganizationSubsidiaryEntity(
                name = request.subsidiary.name,
                email = request.subsidiary.email,
                city = request.subsidiary.city,
                country = request.subsidiary.country,
                phone = request.subsidiary.phone,
                description = request.subsidiary.description,
                parent = organizationEntity
            )
        }

        fun transformOrganizationEntityToOrganizationDto(organizationEntity: OrganizationEntity) = organization {
            id = organizationEntity.id!!
            name = organizationEntity.name
            email = organizationEntity.email
            bio = organizationEntity.bio

            hq = organizationSubsidiary {
                organizationEntity.hq?.id?.let { id = it }
                organizationEntity.hq?.name?.let { name = it }
                organizationEntity.hq?.email?.let { email = it }
                organizationEntity.hq?.let { city = it.city }
                organizationEntity.hq?.let { country = it.country }
                organizationEntity.hq?.phone?.let { phone = it }
                organizationEntity.hq?.description?.let { description = it }
            }

            organizationEntity.logo?.let { logo = it }
            organizationEntity.phone?.let { phone = it }

            webAndSocialUrls = websiteAndSocialMediaUrls {
                organizationEntity.website?.let { website = it }
                organizationEntity.linkedin?.let { linkedin = it }
                organizationEntity.facebook?.let { facebook = it }
                organizationEntity.twitter?.let { twitter = it }
                organizationEntity.skype?.let { skype = it }
                organizationEntity.github?.let { github = it }
            }

            organizationEntity.founded?.let { founded = it }
            organizationEntity.industry?.let { industry = it }
            organizationEntity.founder?.let { founder = it }
            organizationEntity.numberOfEmployees?.let { numberOfEmployees = it }

            benefits.addAll(organizationEntity.benefits.map { it.name })
        }

        fun transformOrganizationSubsidiaryEntityToOrganizationSubsidiaryDto(
            organizationSubsidiaryEntity: OrganizationSubsidiaryEntity
        ) = organizationSubsidiary {
            id = organizationSubsidiaryEntity.id!!
            organizationSubsidiaryEntity.name?.let { name = it }
            organizationSubsidiaryEntity.email?.let { email = it }
            city = organizationSubsidiaryEntity.city
            country = organizationSubsidiaryEntity.country
            organizationSubsidiaryEntity.phone?.let { phone = it }
            organizationSubsidiaryEntity.description?.let { description = it }
        }

        fun buildOrganizationEntityFromOrganizationDto(
            updateOrganizationRequest: UpdateOrganizationRequest,
            organizationEntity: OrganizationEntity) {
            updateOrganizationRequest.name.whenNotEmpty { organizationEntity.name = it }
            updateOrganizationRequest.email.whenNotEmpty { organizationEntity.email = it }
            updateOrganizationRequest.bio.whenNotEmpty { organizationEntity.bio = it }
            updateOrganizationRequest.logo.whenNotEmpty { organizationEntity.logo = it }
            updateOrganizationRequest.phone.whenNotEmpty { organizationEntity.phone = it }
            updateOrganizationRequest.webAndSocialUrls.website.whenNotEmpty { organizationEntity.website = it }
            updateOrganizationRequest.webAndSocialUrls.linkedin.whenNotEmpty { organizationEntity.linkedin = it }
            updateOrganizationRequest.webAndSocialUrls.facebook.whenNotEmpty { organizationEntity.facebook = it }
            updateOrganizationRequest.webAndSocialUrls.twitter.whenNotEmpty { organizationEntity.twitter = it }
            updateOrganizationRequest.webAndSocialUrls.skype.whenNotEmpty { organizationEntity.skype = it }
            updateOrganizationRequest.webAndSocialUrls.github.whenNotEmpty { organizationEntity.github = it }
            updateOrganizationRequest.founded.whenGreaterThanZero { organizationEntity.founded = it }
            updateOrganizationRequest.industry.whenNotEmpty { organizationEntity.industry = it }
            updateOrganizationRequest.founder.whenNotEmpty { organizationEntity.founder = it }
            updateOrganizationRequest.numberOfEmployees.whenGreaterThanZero { organizationEntity.numberOfEmployees = it }
        }

        fun buildOrganizationSubsidiaryEntityFromOrganizationSubsidiaryDto(
            updateOrganizationSubsidiaryRequest: UpdateOrganizationSubsidiaryRequest,
            organizationSubsidiaryEntity: OrganizationSubsidiaryEntity
        ) {
            updateOrganizationSubsidiaryRequest.name.whenNotEmpty { organizationSubsidiaryEntity.name = it }
            updateOrganizationSubsidiaryRequest.email.whenNotEmpty { organizationSubsidiaryEntity.email = it }
            updateOrganizationSubsidiaryRequest.city.whenNotEmpty { organizationSubsidiaryEntity.city = it }
            updateOrganizationSubsidiaryRequest.country.whenNotEmpty { organizationSubsidiaryEntity.country = it }
            updateOrganizationSubsidiaryRequest.phone.whenNotEmpty { organizationSubsidiaryEntity.phone = it }
            updateOrganizationSubsidiaryRequest.description.whenNotEmpty { organizationSubsidiaryEntity.description = it }
        }

        fun transformOrganizationEntityListToOrganizationDtoList(organizationEntityList: List<OrganizationEntity>) : SearchOrganizationByNameResponse {
            val organizationList = mutableListOf<Organization>()

            organizationEntityList.map {
                val organizationDto = transformOrganizationEntityToOrganizationDto(it)
                organizationList.add(organizationDto)
            }
            return searchOrganizationByNameResponse {
                organizations.addAll(organizationList)
            }
        }
    }
}