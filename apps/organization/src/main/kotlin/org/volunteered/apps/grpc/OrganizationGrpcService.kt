package org.volunteered.apps.grpc

import com.google.protobuf.Empty
import net.devh.boot.grpc.server.service.GrpcService
import org.volunteered.apps.service.OrganizationService
import org.volunteered.libs.core.exception.InvalidCountryCodeException
import org.volunteered.libs.core.util.IsoUtil
import org.volunteered.libs.organization.v1.*
import org.volunteered.libs.proto.common.v1.Organization
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary

@GrpcService
class OrganizationGrpcService(
    private val organizationService: OrganizationService
) : OrganizationServiceGrpcKt.OrganizationServiceCoroutineImplBase() {
    override suspend fun createOrganization(request: CreateOrganizationRequest): Organization {
        if (!IsoUtil.isValidISOCountry(request.country))
            throw InvalidCountryCodeException("Invalid country code ${request.country}")

        return organizationService.createOrganization(request)
    }

    override suspend fun createOrganizationSubsidiary(request: CreateOrganizationSubsidiaryRequest): OrganizationSubsidiary {
        if (!IsoUtil.isValidISOCountry(request.subsidiary.country))
            throw InvalidCountryCodeException("Invalid country code ${request.subsidiary.country}")

        return organizationService.createOrganizationSubsidiary(request)
    }

    override suspend fun getOrganizationById(request: GetOrganizationRequest): Organization {
        return organizationService.getOrganizationById(request)
    }

    override suspend fun updateOrganization(request: UpdateOrganizationRequest): Organization {
        return organizationService.updateOrganization(request)
    }

    override suspend fun deleteOrganization(request: DeleteOrganizationRequest): Empty {
        return organizationService.deleteOrganization(request)
    }

    override suspend fun getOrganizationSubsidiaryById(request: GetOrganizationSubsidiaryRequest): OrganizationSubsidiary {
        return organizationService.getOrganizationSubsidiaryById(request)
    }

    override suspend fun deleteOrganizationSubsidiary(request: DeleteOrganizationSubsidiaryRequest): Empty {
        return organizationService.deleteOrganizationSubsidiary(request)
    }

    override suspend fun updateOrganizationSubsidiary(request: OrganizationSubsidiary): OrganizationSubsidiary {
        return organizationService.updateOrganizationSubsidiary(request)
    }

}