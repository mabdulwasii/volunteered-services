package org.volunteered.apps.review.service.impl

import com.google.protobuf.Empty
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.volunteered.apps.review.entity.OrganizationSubsidiaryRatingEntity
import org.volunteered.apps.review.entity.ReplyReviewEntity
import org.volunteered.apps.review.entity.ReviewEntity
import org.volunteered.apps.review.entity.UserHelpfulReviewEntity
import org.volunteered.apps.review.exception.CannotWriteReviewException
import org.volunteered.apps.review.exception.OrganizationSubsidiaryRatingDoesNotExistException
import org.volunteered.apps.review.exception.ReviewDoesNotExistException
import org.volunteered.apps.review.exception.UserHelpfulReviewAlreadyExistException
import org.volunteered.apps.review.repository.OrganizationSubsidiaryRatingRepository
import org.volunteered.apps.review.repository.ReplyReviewRepository
import org.volunteered.apps.review.repository.ReviewRepository
import org.volunteered.apps.review.repository.UserHelpfulReviewRepository
import org.volunteered.apps.review.repository.dao.ReviewSpecifications.Companion.buildEntitySort
import org.volunteered.apps.review.repository.dao.ReviewSpecifications.Companion.buildReviewSpecificationForOrganization
import org.volunteered.apps.review.repository.dao.ReviewSpecifications.Companion.buildReviewSpecificationForOrganizationSubsidiary
import org.volunteered.apps.review.repository.dao.ReviewSpecifications.Companion.buildReviewSpecificationForUser
import org.volunteered.apps.review.service.ReviewService
import org.volunteered.apps.review.util.DtoTransformer
import org.volunteered.apps.review.util.RatingCalculator
import org.volunteered.apps.review.util.StringEncoder
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.common.v1.PaginationRequest
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.common.v1.paginationResponse
import org.volunteered.libs.proto.organization.v1.OrganizationServiceGrpcKt
import org.volunteered.libs.proto.organization.v1.getOrganizationOrganizationSubsidiaryIdsRequest
import org.volunteered.libs.proto.organization.v1.getOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.review.v1.DeleteReviewRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationReviewsRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationSubsidiaryRatingRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationSubsidiaryReviewsRequest
import org.volunteered.libs.proto.review.v1.GetReviewRepliesRequest
import org.volunteered.libs.proto.review.v1.GetReviewRepliesResponse
import org.volunteered.libs.proto.review.v1.GetReviewsResponse
import org.volunteered.libs.proto.review.v1.GetUserReviewsRequest
import org.volunteered.libs.proto.review.v1.MarkReviewAsHelpfulRequest
import org.volunteered.libs.proto.review.v1.Rating
import org.volunteered.libs.proto.review.v1.ReplyReviewRequest
import org.volunteered.libs.proto.review.v1.Review
import org.volunteered.libs.proto.review.v1.ReviewReply
import org.volunteered.libs.proto.review.v1.UpdateReviewRequest
import org.volunteered.libs.proto.review.v1.WriteReviewRequest
import org.volunteered.libs.proto.review.v1.getReviewRepliesResponse
import org.volunteered.libs.proto.review.v1.getReviewsResponse
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt
import org.volunteered.libs.proto.user.v1.getUserByIdRequest

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class ReviewServiceImpl(
    private val userServiceStub: UserServiceGrpcKt.UserServiceCoroutineStub,
    private val organizationServiceStub: OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub,
    private val reviewRepository: ReviewRepository,
    private val replyReviewRepository: ReplyReviewRepository,
    private val organizationSubsidiaryRatingRepository: OrganizationSubsidiaryRatingRepository,
    private val ratingCalculator: RatingCalculator,
    private val helpfulReviewRepository: UserHelpfulReviewRepository
) : ReviewService {
    override suspend fun writeReview(request: WriteReviewRequest): Review {
        val userId: String = if (request.anonymous) {
            StringEncoder.hashValue(request.userId.toString())
        } else {
            request.userId.toString()
        }

        ensureReviewDoesNotExist(userId, request.organizationSubsidiaryId)

        val user = getUserById(request.userId)
        val organizationSubsidiary = getOrganizationSubsidiaryById(request.organizationSubsidiaryId)

        val reviewEntity =
            DtoTransformer.transformWriteReviewRequestToReviewEntity(
                request,
                organizationSubsidiary,
                userId
            )
        val savedReview = reviewRepository.save(reviewEntity)

        saveOrUpdateOrganizationRating(savedReview)

        return DtoTransformer.transformReviewEntityToReviewDto(savedReview, user)
    }

    override suspend fun getOrganizationSubsidiaryRating(request: GetOrganizationSubsidiaryRatingRequest): Rating {
        organizationSubsidiaryRatingRepository.findByOrganizationSubsidiaryId(request.organizationSubsidiaryId)?.let {
            return DtoTransformer.transformRatingEntityToRatingDto(it)
        } ?: throw OrganizationSubsidiaryRatingDoesNotExistException("Organization subsidiary rating does not exist")
    }

    override suspend fun updateReview(request: UpdateReviewRequest): Review {
        val reviewEntity = reviewRepository.findByIdOrNull(request.id)

        reviewEntity?.let {
            val user: User? = getUserFromReviewEntity(it)
            DtoTransformer.buildReviewEntityFromReviewDto(it, request)
            val savedReviewEntity = reviewRepository.save(it)
            return DtoTransformer.transformReviewEntityToReviewDto(savedReviewEntity, user)
        } ?: throw ReviewDoesNotExistException("Review does not exist")
    }

    override suspend fun getOrganizationReviews(request: GetOrganizationReviewsRequest): GetReviewsResponse {
        val pageable = PageRequest.of(
            request.pagination.page,
            request.pagination.limitPerPage,
            buildEntitySort(request.sort)
        )
        val specification = buildReviewSpecificationForOrganization(
            request.filter,
            getOrganizationSubsidiaryIdList(request)
        )
        val retrievedReviews = reviewRepository.findAll(specification, pageable)
        return transformReviewEntityListToReviewDtoList(retrievedReviews, request.pagination)
    }

    override suspend fun getOrganizationSubsidiaryReviews(request: GetOrganizationSubsidiaryReviewsRequest): GetReviewsResponse {
        val pageable = PageRequest.of(
            request.pagination.page,
            request.pagination.limitPerPage,
            buildEntitySort(request.sort)
        )
        val specification = buildReviewSpecificationForOrganizationSubsidiary(
            request.filter,
            request.organizationSubsidiaryId
        )
        val retrievedReviews = reviewRepository.findAll(specification, pageable)
        return transformReviewEntityListToReviewDtoList(retrievedReviews, request.pagination)
    }

    override suspend fun getUserReviews(request: GetUserReviewsRequest): GetReviewsResponse {
        val pageable = PageRequest.of(
            request.pagination.page,
            request.pagination.limitPerPage,
            buildEntitySort(request.sort)
        )
        val specification = buildReviewSpecificationForUser(
            request.filter,
            request.userId,
            StringEncoder.hashValue(request.userId.toString())
        )

        val retrievedReviews = reviewRepository.findAll(specification, pageable)
        return transformReviewEntityListToReviewDtoList(retrievedReviews, request.pagination)
    }

    override suspend fun markReviewAsHelpful(request: MarkReviewAsHelpfulRequest): Empty {
        ensureUserHasNotMarkedReviewAsHelpful(request.userId, request.reviewId)
        reviewRepository.findByIdOrNull(request.reviewId)?.let {
            it.helpfulCount = +1
            reviewRepository.save(it)
            saveUserHelpfulReview(request.userId, it.id!!)
            return Empty.getDefaultInstance()
        } ?: throw ReviewDoesNotExistException("Review does not exist")
    }

    override suspend fun replyReview(request: ReplyReviewRequest): ReviewReply {
        val user = getUserById(request.userId)

        val reviewEntity = reviewRepository.findByIdOrNull(request.reviewId)
        reviewEntity?.let {
            val replyReviewEntity = DtoTransformer.transformReplyReviewRequestToReplyReviewEntity(
                request,
                user,
                it
            )
            val savedReplyReviewEntity = replyReviewRepository.save(replyReviewEntity)

            return DtoTransformer.transformReplyReviewEntityToReplyReviewDto(savedReplyReviewEntity, user)
        } ?: throw ReviewDoesNotExistException("Review does not exist")
    }

    override suspend fun deleteReview(request: DeleteReviewRequest): Empty {
        reviewRepository.deleteById(request.id)
        return Empty.getDefaultInstance()
    }

    override suspend fun getReviewReplies(request: GetReviewRepliesRequest): GetReviewRepliesResponse {
        val retrievedReviewReplies = replyReviewRepository.findAllByReviewId(request.reviewId)
        return transformReviewReplyListToGetReviewRepliesResponse(retrievedReviewReplies)
    }

    private fun ensureUserHasNotMarkedReviewAsHelpful(userId: Long, reviewId: Long) {
        val exists = helpfulReviewRepository.existsByUserIdAndReviewId(userId, reviewId)
        if (exists) {
            throw UserHelpfulReviewAlreadyExistException("Review has already been marked helpful")
        }
    }

    private suspend fun saveUserHelpfulReview(userId: Long, reviewId: Long) {
        helpfulReviewRepository.save(
            UserHelpfulReviewEntity(
                userId = userId,
                reviewId = reviewId
            )
        )
    }

    private suspend fun getUserById(userId: Long): User {
        return userServiceStub.getUserById(
            getUserByIdRequest { id = userId }
        )
    }

    private suspend fun getOrganizationSubsidiaryById(organizationSubsidiaryId: Long): OrganizationSubsidiary {
        return organizationServiceStub.getOrganizationSubsidiaryById(
            getOrganizationSubsidiaryRequest { id = organizationSubsidiaryId }
        )
    }

    private suspend fun saveOrUpdateOrganizationRating(reviewEntity: ReviewEntity) {
        val organizationSubsidiaryRatingEntity =
            organizationSubsidiaryRatingRepository.findByOrganizationSubsidiaryId(reviewEntity.organizationSubsidiaryId)
                ?: OrganizationSubsidiaryRatingEntity(
                    organizationSubsidiaryId = reviewEntity.organizationSubsidiaryId,
                    rating = 0.0,
                    verifiedRatingCount = 0,
                    unverifiedRatingCount = 0
                )

        organizationSubsidiaryRatingEntity.rating = ratingCalculator.recomputeOrganizationSubsidiaryRating(
            organizationSubsidiaryRatingEntity,
            reviewEntity.rating,
            reviewEntity.verified
        )

        if (reviewEntity.verified) {
            organizationSubsidiaryRatingEntity.verifiedRatingCount += 1
        } else {
            organizationSubsidiaryRatingEntity.unverifiedRatingCount += 1
        }

        organizationSubsidiaryRatingRepository.save(organizationSubsidiaryRatingEntity)
    }

    private fun ensureReviewDoesNotExist(
        userId: String,
        organizationSubsidiaryId: Long
    ) {
        val reviewExist = reviewRepository.existsByUserIdAndOrganizationSubsidiaryId(
            userId,
            organizationSubsidiaryId
        )

        if (reviewExist) {
            throw CannotWriteReviewException("You cannot write multiple reviews for an organization")
        }
    }

    private suspend fun transformReviewReplyListToGetReviewRepliesResponse(reviewReplyList: List<ReplyReviewEntity>):
        GetReviewRepliesResponse {
            return getReviewRepliesResponse {
                val reviewReplyDtoList = mutableListOf<ReviewReply>()
                reviewReplyList.forEach {
                    val user = getUserById(it.userId)
                    val reviewReplyDto =
                        DtoTransformer.transformReplyReviewEntityToReplyReviewDto(it, user)
                    reviewReplyDtoList.add(reviewReplyDto)
                }
                reviewReplies.addAll(reviewReplyDtoList)
            }
        }

    private suspend fun transformReviewEntityListToReviewDtoList(
        reviewEntityList: Page<ReviewEntity>,
        paginationRequest: PaginationRequest
    ): GetReviewsResponse {
        return getReviewsResponse {
            val reviewDtoList = mutableListOf<Review>()
            reviewEntityList.forEach {
                val user: User? = getUserFromReviewEntity(it)
                val reviewDto = DtoTransformer.transformReviewEntityToReviewDto(it, user)
                reviewDtoList.add(reviewDto)
            }
            val paginationResponse = paginationResponse {
                total = reviewEntityList.totalElements
                limitPerPage = paginationRequest.limitPerPage
                page = paginationRequest.page
            }
            reviews.addAll(reviewDtoList)
            pagination = paginationResponse
        }
    }

    private suspend fun getUserFromReviewEntity(reviewEntity: ReviewEntity): User? {
        val userId = reviewEntity.userId.toLongOrNull()
        var user: User? = null
        userId?.let { user = getUserById(userId) }
        return user
    }

    private suspend fun getOrganizationSubsidiaryIdList(request: GetOrganizationReviewsRequest): List<Long> {
        val organizationSubsidiaryIds = organizationServiceStub.getOrganizationOrganizationSubsidiaryIds(
            getOrganizationOrganizationSubsidiaryIdsRequest {
                organizationId = request.organizationId
            }
        ).organizationSubsidiaryIdsList
        return organizationSubsidiaryIds
    }
}
