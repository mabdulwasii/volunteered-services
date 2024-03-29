package org.volunteered.apps.organization.service.impl

import com.google.protobuf.BoolValue
import com.google.protobuf.Empty
import com.google.protobuf.boolValue
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.volunteered.apps.organization.entity.OrganizationJobTitleEntity
import org.volunteered.apps.organization.entity.OrganizationSubsidiaryEntity
import org.volunteered.apps.organization.exception.CreatorDoesNotExistException
import org.volunteered.apps.organization.exception.OrganizationAlreadyExistsException
import org.volunteered.apps.organization.exception.OrganizationDoesNotExistException
import org.volunteered.apps.organization.repository.BenefitRepository
import org.volunteered.apps.organization.repository.OrganizationJobTitleRepository
import org.volunteered.apps.organization.repository.OrganizationRepository
import org.volunteered.apps.organization.repository.OrganizationSubsidiaryRepository
import org.volunteered.apps.organization.service.OrganizationService
import org.volunteered.apps.organization.util.DtoTransformer
import org.volunteered.libs.core.extension.whenGreaterThanZero
import org.volunteered.libs.core.extension.whenNotEmpty
import org.volunteered.libs.proto.common.v1.Id
import org.volunteered.libs.proto.common.v1.Organization
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.common.v1.id
import org.volunteered.libs.proto.organization.v1.CreateOrganizationRequest
import org.volunteered.libs.proto.organization.v1.CreateOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.DeleteOrganizationRequest
import org.volunteered.libs.proto.organization.v1.DeleteOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.GetOrganizationJobTitlesResponse
import org.volunteered.libs.proto.organization.v1.GetOrganizationOrganizationSubsidiaryIdsRequest
import org.volunteered.libs.proto.organization.v1.GetOrganizationOrganizationSubsidiaryIdsResponse
import org.volunteered.libs.proto.organization.v1.GetOrganizationRequest
import org.volunteered.libs.proto.organization.v1.GetOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.organization.v1.OrganizationJobTitle
import org.volunteered.libs.proto.organization.v1.SearchOrganizationByNameRequest
import org.volunteered.libs.proto.organization.v1.SearchOrganizationByNameResponse
import org.volunteered.libs.proto.organization.v1.UpdateOrganizationRequest
import org.volunteered.libs.proto.organization.v1.getOrganizationJobTitlesResponse
import org.volunteered.libs.proto.organization.v1.getOrganizationOrganizationSubsidiaryIdsResponse
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class OrganizationServiceImpl(
    private val organizationRepository: OrganizationRepository,
    private val organizationSubsidiaryRepository: OrganizationSubsidiaryRepository,
    private val benefitRepository: BenefitRepository,
    private val userServiceStub: UserServiceGrpcKt.UserServiceCoroutineStub,
    private val jobTitleRepository: OrganizationJobTitleRepository,
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

            return DtoTransformer.transformOrganizationSubsidiaryEntityToOrganizationSubsidiaryDto(
                updatedOrganizationSubsidiaryEntity
            )
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
        return organizationRepository.findByIdOrNull(request.id)?.let {
            DtoTransformer.transformOrganizationEntityToOrganizationDto(it)
        } ?: throw OrganizationDoesNotExistException("Organization does not exist")
    }

    @Transactional
    override suspend fun updateOrganization(request: UpdateOrganizationRequest): Organization {
        organizationRepository.findByIdOrNull(request.id)?.let {
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
        return organizationSubsidiaryRepository.findByIdOrNull(request.id)?.let {
            DtoTransformer.transformOrganizationSubsidiaryEntityToOrganizationSubsidiaryDto(it)
        } ?: throw OrganizationDoesNotExistException("Organization Subsidiary does not exist")
    }

    override suspend fun organizationSubsidiaryExistsById(request: Id): BoolValue {
        return boolValue {
            value = organizationSubsidiaryRepository.existsById(request.id)
        }
    }

    override suspend fun getOrganizationOrganizationSubsidiaryIds(request: GetOrganizationOrganizationSubsidiaryIdsRequest): GetOrganizationOrganizationSubsidiaryIdsResponse {
        return getOrganizationOrganizationSubsidiaryIdsResponse {
            organizationSubsidiaryIds.addAll(
                organizationSubsidiaryRepository
                    .findAllByParentId(request.organizationId)
                    .mapNotNull { it.id }
            )
        }
    }

    override suspend fun searchOrganizationByName(request: SearchOrganizationByNameRequest): SearchOrganizationByNameResponse {
        val organizationEntityList = organizationRepository.findByNameLike(request.name)
        return DtoTransformer.transformOrganizationEntityListToOrganizationDtoList(organizationEntityList)
    }

    override suspend fun createOrganizationJobTitle(request: OrganizationJobTitle): OrganizationJobTitle {
        if (!organizationRepository.existsById(request.organizationId))
            throw OrganizationDoesNotExistException("Organization does not exist")

        val organizationJobTitleEntity = OrganizationJobTitleEntity(
            organizationId = request.organizationId,
            title = request.title
        )
        return DtoTransformer.transformOrganizationJobTitleEntityToOrganizationJobTitleDto(
            jobTitleRepository.save(organizationJobTitleEntity)
        )
    }

    override suspend fun getOrganizationJobTitles(request: Id): GetOrganizationJobTitlesResponse {
        return getOrganizationJobTitlesResponse {
            titles.addAll(
                DtoTransformer.transformOrganizationJobTileEntityListToOrganizationJobTitleDtoList(
                    jobTitleRepository.findAllByOrganizationId(request.id)
                )
            )
        }
    }

    private suspend fun ensureCreatorExists(creatorId: Long) {
        val creatorExists = userServiceStub.existsById(
            id { id = creatorId }
        ).value

        if (!creatorExists)
            throw CreatorDoesNotExistException("Creator does not exist")
    }
}
