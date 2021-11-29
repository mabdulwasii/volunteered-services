package org.volunteered.apps.service

import com.google.protobuf.Empty
import org.volunteered.libs.organization.v1.CreateOrganizationRequest
import org.volunteered.libs.organization.v1.CreateOrganizationSubsidiaryRequest
import org.volunteered.libs.organization.v1.DeleteOrganizationRequest
import org.volunteered.libs.organization.v1.GetOrganizationRequest
import org.volunteered.libs.proto.common.v1.Organization
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary

interface OrganizationService {
    suspend fun createOrganization(request: CreateOrganizationRequest): Organization
    suspend fun createOrganizationSubsidiary(request: CreateOrganizationSubsidiaryRequest): OrganizationSubsidiary
    suspend fun getOrganizationById(request: GetOrganizationRequest): Organization
    suspend fun updateOrganization(request: Organization): Organization
    suspend fun deleteOrganization(request: DeleteOrganizationRequest): Empty
}