package org.volunteered.apps.service.impl

import com.google.protobuf.Empty
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.volunteered.apps.exception.CreatorDoesNotExistException
import org.volunteered.apps.exception.OrganizationAlreadyExistsException
import org.volunteered.apps.exception.OrganizationDoesNotExistException
import org.volunteered.apps.repository.OrganizationRepository
import org.volunteered.apps.repository.OrganizationSubsidiaryRepository
import org.volunteered.apps.service.OrganizationService
import org.volunteered.apps.util.DtoTransformer
import org.volunteered.libs.organization.v1.CreateOrganizationRequest
import org.volunteered.libs.organization.v1.CreateOrganizationSubsidiaryRequest
import org.volunteered.libs.organization.v1.DeleteOrganizationRequest
import org.volunteered.libs.organization.v1.GetOrganizationRequest
import org.volunteered.libs.proto.common.v1.Organization
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.user.v1.UserServiceGrpcKt
import org.volunteered.libs.user.v1.existsByIdRequest

@Service
class OrganizationServiceImpl(
    private val organizationRepository: OrganizationRepository,
    private val organizationSubsidiaryRepository: OrganizationSubsidiaryRepository,
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val userServiceStub: UserServiceGrpcKt.UserServiceCoroutineStub,
) : OrganizationService {
    override suspend fun createOrganization(request: CreateOrganizationRequest): Organization {
        val creatorExists = userServiceStub.existsById(
            existsByIdRequest { id = request.creatorId }
        ).value
        if (!creatorExists)
            throw CreatorDoesNotExistException("Creator does not exist")

        if (organizationRepository.existsByEmail(request.email))
            throw OrganizationAlreadyExistsException("Organization already exists")

        val organizationEntity = DtoTransformer.transformCreateOrganizationRequestToOrganizationEntity(request)
        organizationSubsidiaryRepository.save(organizationEntity.hq)
        val createdOrganizationEntity = organizationRepository.save(organizationEntity)

        return DtoTransformer.transformOrganizationEntityToOrganizationDto(createdOrganizationEntity)
    }

    override suspend fun createOrganizationSubsidiary(request: CreateOrganizationSubsidiaryRequest): OrganizationSubsidiary {
//        if (!organizationRepository.existsById(request.organizationId))
//            throw OrganizationDoesNotExistException("Organization does not exist")

        TODO("Not yet implemented")
    }

    override suspend fun getOrganizationById(request: GetOrganizationRequest): Organization {
        val organizationEntity = organizationRepository.findByIdOrNull(request.id)

        return organizationEntity?.let { DtoTransformer.transformOrganizationEntityToOrganizationDto(it) }
            ?: throw OrganizationDoesNotExistException("Organization does not exist")
    }

    override suspend fun updateOrganization(request: Organization): Organization {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOrganization(request: DeleteOrganizationRequest): Empty {
        TODO("Not yet implemented")
    }
}