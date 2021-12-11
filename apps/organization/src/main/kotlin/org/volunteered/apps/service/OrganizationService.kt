package org.volunteered.apps.service

import com.google.protobuf.Empty
import org.volunteered.libs.proto.common.v1.Organization
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.organization.v1.CreateOrganizationRequest
import org.volunteered.libs.proto.organization.v1.CreateOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.DeleteOrganizationRequest
import org.volunteered.libs.proto.organization.v1.DeleteOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.GetOrganizationRequest
import org.volunteered.libs.proto.organization.v1.GetOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.SearchOrganizationByNameRequest
import org.volunteered.libs.proto.organization.v1.SearchOrganizationByNameResponse
import org.volunteered.libs.proto.organization.v1.UpdateOrganizationRequest

interface OrganizationService {
    suspend fun createOrganization(request: CreateOrganizationRequest): Organization
    suspend fun createOrganizationSubsidiary(request: CreateOrganizationSubsidiaryRequest): OrganizationSubsidiary
    suspend fun getOrganizationById(request: GetOrganizationRequest): Organization
    suspend fun updateOrganization(request: UpdateOrganizationRequest): Organization
    suspend fun deleteOrganization(request: DeleteOrganizationRequest): Empty
    suspend fun deleteOrganizationSubsidiary(request: DeleteOrganizationSubsidiaryRequest): Empty
    suspend fun getOrganizationSubsidiaryById(request: GetOrganizationSubsidiaryRequest): OrganizationSubsidiary
    suspend fun updateOrganizationSubsidiary(request: OrganizationSubsidiary): OrganizationSubsidiary
    suspend fun searchOrganizationByName(request: SearchOrganizationByNameRequest): SearchOrganizationByNameResponse
}