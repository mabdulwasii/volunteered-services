package org.volunteered.apps.util

import org.volunteered.apps.entity.OrganizationEntity
import org.volunteered.apps.entity.OrganizationSubsidiaryEntity
import org.volunteered.libs.core.extension.whenNotEmpty
import org.volunteered.libs.core.extension.whenNotZero
import org.volunteered.libs.organization.v1.CreateOrganizationRequest
import org.volunteered.libs.organization.v1.CreateOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.common.v1.*

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

        fun buildOrganizationEntityFromOrganizationDto(organization: Organization, organizationEntity: OrganizationEntity) {
            organization.name.whenNotEmpty { organizationEntity.name = it }
            organization.email.whenNotEmpty { organizationEntity.email = it }
            organization.bio.whenNotEmpty { organizationEntity.bio = it }
            organization.logo.whenNotEmpty { organizationEntity.logo = it }
            organization.phone.whenNotEmpty { organizationEntity.phone = it }
            organization.webAndSocialUrls.website.whenNotEmpty { organizationEntity.website = it }
            organization.webAndSocialUrls.linkedin.whenNotEmpty { organizationEntity.linkedin = it }
            organization.webAndSocialUrls.facebook.whenNotEmpty { organizationEntity.facebook = it }
            organization.webAndSocialUrls.twitter.whenNotEmpty { organizationEntity.twitter = it }
            organization.webAndSocialUrls.skype.whenNotEmpty { organizationEntity.skype = it }
            organization.webAndSocialUrls.github.whenNotEmpty { organizationEntity.github = it }
            organization.founded.whenNotZero { organizationEntity.founded = it }
            organization.industry.whenNotEmpty { organizationEntity.industry = it }
            organization.founder.whenNotEmpty { organizationEntity.founder = it }
            organization.numberOfEmployees.whenNotZero { organizationEntity.numberOfEmployees = it }
            organization.hq.name.whenNotEmpty { organizationEntity.hq?.name = it }
            organization.hq.email.whenNotEmpty { organizationEntity.hq?.email = it }
            organization.hq.city.whenNotEmpty { organizationEntity.hq?.city = it }
            organization.hq.country.whenNotEmpty { organizationEntity.hq?.country = it }
            organization.hq.phone.whenNotEmpty { organizationEntity.hq?.phone = it }
            organization.hq.description.whenNotEmpty { organizationEntity.hq?.description = it }
        }

        fun buildOrganizationSubsidiaryEntityFromOrganizationSubsidiaryDto(
            organizationSubsidiary: OrganizationSubsidiary,
            organizationSubsidiaryEntity: OrganizationSubsidiaryEntity
        ) {
            organizationSubsidiary.name.whenNotEmpty { organizationSubsidiaryEntity.name = it }
            organizationSubsidiary.email.whenNotEmpty { organizationSubsidiaryEntity.email = it }
            organizationSubsidiary.city.whenNotEmpty { organizationSubsidiaryEntity.city = it }
            organizationSubsidiary.country.whenNotEmpty { organizationSubsidiaryEntity.country = it }
            organizationSubsidiary.phone.whenNotEmpty { organizationSubsidiaryEntity.phone = it }
            organizationSubsidiary.description.whenNotEmpty { organizationSubsidiaryEntity.description = it }
        }
    }
}