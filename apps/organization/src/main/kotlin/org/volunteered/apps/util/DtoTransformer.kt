package org.volunteered.apps.util

import org.volunteered.apps.entity.OrganizationEntity
import org.volunteered.apps.entity.OrganizationSubsidiary
import org.volunteered.libs.organization.v1.CreateOrganizationRequest
import org.volunteered.libs.proto.common.v1.organization
import org.volunteered.libs.proto.common.v1.organizationSubsidiary
import org.volunteered.libs.proto.common.v1.websiteAndSocialMediaUrls

class DtoTransformer {
    companion object {
        fun transformCreateOrganizationRequestToOrganizationEntity(request: CreateOrganizationRequest): OrganizationEntity {
            return OrganizationEntity(
                name = request.name,
                email = request.email,
                bio = request.bio,
                hq = OrganizationSubsidiary(
                    city = request.city,
                    country = request.country
                )
            )
        }

        fun transformOrganizationEntityToOrganizationDto(organizationEntity: OrganizationEntity) = organization {
            id = organizationEntity.id!!
            name = organizationEntity.name
            email = organizationEntity.email
            bio = organizationEntity.bio

            hq = organizationSubsidiary {
                organizationEntity.hq.name?.let { name = it }
                organizationEntity.hq.email?.let { email = it }
                city = organizationEntity.hq.city
                country = organizationEntity.hq.country
                phones.addAll(organizationEntity.hq.phones)
                organizationEntity.hq.description?.let { description = it }
            }

            organizationEntity.logo?.let { logo = it }
            phones.addAll(organizationEntity.phones)

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
    }
}