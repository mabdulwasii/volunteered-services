package org.volunteered.apps.service.impl

import com.google.protobuf.BoolValue
import com.google.protobuf.Empty
import com.google.protobuf.boolValue
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.volunteered.apps.entity.OrganizationSubsidiaryEntity
import org.volunteered.apps.exception.CreatorDoesNotExistException
import org.volunteered.apps.exception.OrganizationAlreadyExistsException
import org.volunteered.apps.exception.OrganizationDoesNotExistException
import org.volunteered.apps.repository.BenefitRepository
import org.volunteered.apps.repository.OrganizationRepository
import org.volunteered.apps.repository.OrganizationSubsidiaryRepository
import org.volunteered.apps.service.OrganizationService
import org.volunteered.apps.util.DtoTransformer
import org.volunteered.libs.core.extension.whenGreaterThanZero
import org.volunteered.libs.core.extension.whenNotEmpty
import org.volunteered.libs.proto.common.v1.ExistsByIdRequest
import org.volunteered.libs.proto.common.v1.Organization
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.common.v1.existsByIdRequest
import org.volunteered.libs.proto.organization.v1.CreateOrganizationRequest
import org.volunteered.libs.proto.organization.v1.CreateOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.DeleteOrganizationRequest
import org.volunteered.libs.proto.organization.v1.DeleteOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.GetOrganizationRequest
import org.volunteered.libs.proto.organization.v1.GetOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.SearchOrganizationByNameRequest
import org.volunteered.libs.proto.organization.v1.SearchOrganizationByNameResponse
import org.volunteered.libs.proto.organization.v1.UpdateOrganizationRequest
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class OrganizationServiceImpl(
    private val organizationRepository: OrganizationRepository,
    private val organizationSubsidiaryRepository: OrganizationSubsidiaryRepository,
    private val benefitRepository: BenefitRepository,
    private val userServiceStub: UserServiceGrpcKt.UserServiceCoroutineStub,
) : OrganizationService {
    override suspend fun createOrganization(request: CreateOrganizationRequest): Organization {
        ensureCreatorExists(request.creatorId)

        if (organizationRepository.existsByEmail(request.email))
            throw OrganizationAlreadyExistsException("Organization already exists")

        val organizationEntity = DtoTransformer.transformCreateOrganizationRequestToOrganizationEntity(request)
        val hq = OrganizationSubsidiaryEntity(
            city = request.city,
            country = request.country,
            parent = organizationEntity
        )
        organizationEntity.hq = hq

        val createdOrganizationEntity = organizationRepository.save(organizationEntity)
        organizationSubsidiaryRepository.save(hq)

        return DtoTransformer.transformOrganizationEntityToOrganizationDto(createdOrganizationEntity)
    }

    @Transactional
    override suspend fun updateOrganizationSubsidiary(request: OrganizationSubsidiary): OrganizationSubsidiary {
        val organizationSubsidiaryEntity = organizationSubsidiaryRepository.findByIdOrNull(request.id)
        organizationSubsidiaryEntity?.let {
            DtoTransformer.buildOrganizationSubsidiaryEntityFromOrganizationSubsidiaryDto(request, it)
            val updatedOrganizationSubsidiaryEntity = organizationSubsidiaryRepository.save(it)

            return DtoTransformer.transformOrganizationSubsidiaryEntityToOrganizationSubsidiaryDto(updatedOrganizationSubsidiaryEntity)
        } ?: throw OrganizationDoesNotExistException("Organization subsidiary does not exist")
    }

    override suspend fun createOrganizationSubsidiary(request: CreateOrganizationSubsidiaryRequest): OrganizationSubsidiary {
        ensureCreatorExists(request.creatorId)

        val organizationEntity = organizationRepository.findByIdOrNull(request.organizationId)

        organizationEntity?.let {
            val organizationSubsidiaryEntity = DtoTransformer.transformCreateOrganizationSubsidiaryRequestToOrganizationSubsidiaryEntity(
                request,
                organizationEntity
            )
            val createdOrganizationSubsidiary = organizationSubsidiaryRepository.save(organizationSubsidiaryEntity)

            return DtoTransformer.transformOrganizationSubsidiaryEntityToOrganizationSubsidiaryDto(createdOrganizationSubsidiary)
        } ?: throw OrganizationDoesNotExistException("Organization does not exist")
    }

    override suspend fun getOrganizationById(request: GetOrganizationRequest): Organization {
        val organizationEntity = organizationRepository.findByIdOrNull(request.id)

        return organizationEntity?.let { DtoTransformer.transformOrganizationEntityToOrganizationDto(it) }
            ?: throw OrganizationDoesNotExistException("Organization does not exist")
    }

    @Transactional
    override suspend fun updateOrganization(request: UpdateOrganizationRequest): Organization {
        val organizationEntity = organizationRepository.findByIdOrNull(request.id)
        organizationEntity?.let {
            request.benefitsList.whenNotEmpty { benefits ->
                it.benefits = benefitRepository.findByNameIn(benefits).toSet()
            }
            request.hqId.whenGreaterThanZero { hqId ->
                organizationSubsidiaryRepository.findByIdOrNull(hqId)?.let { organizationHqEntity ->
                    it.hq = organizationHqEntity
                } ?: throw OrganizationDoesNotExistException("Organization hq does not exist")
            }

            DtoTransformer.buildOrganizationEntityFromOrganizationDto(request, it)
            val updatedOrganizationEntity = organizationRepository.save(it)

            return DtoTransformer.transformOrganizationEntityToOrganizationDto(updatedOrganizationEntity)
        } ?: throw OrganizationDoesNotExistException("Organization does not exist")
    }

    override suspend fun deleteOrganization(request: DeleteOrganizationRequest): Empty {
        organizationRepository.deleteById(request.id)
        return Empty.getDefaultInstance()
    }

    override suspend fun deleteOrganizationSubsidiary(request: DeleteOrganizationSubsidiaryRequest): Empty {
        organizationSubsidiaryRepository.deleteById(request.id)
        return Empty.getDefaultInstance()
    }

    override suspend fun getOrganizationSubsidiaryById(request: GetOrganizationSubsidiaryRequest): OrganizationSubsidiary {
        val organizationSubsidiaryEntity = organizationSubsidiaryRepository.findByIdOrNull(request.id)

        return organizationSubsidiaryEntity?.let {
            DtoTransformer.transformOrganizationSubsidiaryEntityToOrganizationSubsidiaryDto(it)
        } ?: throw OrganizationDoesNotExistException("Organization Subsidiary does not exist")
    }

    override suspend fun organizationSubsidiaryExistsById(request: ExistsByIdRequest): BoolValue {
        return boolValue {
            value =  organizationSubsidiaryRepository.existsById(request.id)
        }
    }

    override suspend fun searchOrganizationByName(request: SearchOrganizationByNameRequest): SearchOrganizationByNameResponse {
        val organizationEntityList = organizationRepository.findByNameLike(request.name)
        organizationEntityList?.let {
            return DtoTransformer.transformOrganizationEntityListToOrganizationDtoList(organizationEntityList)
        }?: throw OrganizationDoesNotExistException("Organization does not exist")
    }

    private suspend fun ensureCreatorExists(creatorId: Long) {
        val creatorExists = userServiceStub.existsById(
            existsByIdRequest { id = creatorId }
        ).value

        if (!creatorExists)
            throw CreatorDoesNotExistException("Creator does not exist")
    }
}
