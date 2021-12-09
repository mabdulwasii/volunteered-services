package org.volunteered.apps.service

import com.google.protobuf.Empty
import org.volunteered.libs.organization.v1.*
import org.volunteered.libs.proto.common.v1.Organization
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary

interface OrganizationService {
    suspend fun createOrganization(request: CreateOrganizationRequest): Organization
    suspend fun createOrganizationSubsidiary(request: CreateOrganizationSubsidiaryRequest): OrganizationSubsidiary
    suspend fun getOrganizationById(request: GetOrganizationRequest): Organization
    suspend fun updateOrganization(request: UpdateOrganizationRequest): Organization
    suspend fun deleteOrganization(request: DeleteOrganizationRequest): Empty
    suspend fun deleteOrganizationSubsidiary(request: DeleteOrganizationSubsidiaryRequest): Empty
    suspend fun getOrganizationSubsidiaryById(request: GetOrganizationSubsidiaryRequest): OrganizationSubsidiary
    suspend fun updateOrganizationSubsidiary(request: OrganizationSubsidiary): OrganizationSubsidiary
}