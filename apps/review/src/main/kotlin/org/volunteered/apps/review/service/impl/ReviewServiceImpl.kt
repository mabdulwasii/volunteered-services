package org.volunteered.apps.review.service.impl

import com.google.protobuf.Empty
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.volunteered.apps.review.entity.RatingEntity
import org.volunteered.apps.review.entity.ReviewEntity
import org.volunteered.apps.review.exception.CannotWriteReviewException
import org.volunteered.apps.review.exception.OrganizationSubsidiaryRatingDoesNotExistException
import org.volunteered.apps.review.exception.RatingConfigDoesNotExistException
import org.volunteered.apps.review.exception.ReviewDoesNotExistException
import org.volunteered.apps.review.repository.RatingConfigRepository
import org.volunteered.apps.review.repository.RatingRepository
import org.volunteered.apps.review.repository.ReplyReviewRepository
import org.volunteered.apps.review.repository.ReviewRepository
import org.volunteered.apps.review.service.ReviewService
import org.volunteered.apps.review.util.DtoTransformer
import org.volunteered.apps.review.util.RatingCalculator
import org.volunteered.apps.review.util.StringEncoder
import org.volunteered.libs.proto.organization.v1.OrganizationServiceGrpcKt
import org.volunteered.libs.proto.organization.v1.getOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.review.v1.CreateRatingConfigRequest
import org.volunteered.libs.proto.review.v1.DeleteReviewRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationReviewsRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationSubsidiaryRatingRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationSubsidiaryReviewsRequest
import org.volunteered.libs.proto.review.v1.GetRatingConfigRequest
import org.volunteered.libs.proto.review.v1.GetReviewsResponse
import org.volunteered.libs.proto.review.v1.MarkReviewAsHelpfulRequest
import org.volunteered.libs.proto.review.v1.Rating
import org.volunteered.libs.proto.review.v1.RatingConfig
import org.volunteered.libs.proto.review.v1.ReplyReviewRequest
import org.volunteered.libs.proto.review.v1.Review
import org.volunteered.libs.proto.review.v1.ReviewReply
import org.volunteered.libs.proto.review.v1.UpdateRatingConfigRequest
import org.volunteered.libs.proto.review.v1.UpdateReviewRequest
import org.volunteered.libs.proto.review.v1.WriteReviewRequest
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt
import org.volunteered.libs.proto.user.v1.getUserByIdRequest

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class ReviewServiceImpl(
    private val userServiceStub: UserServiceGrpcKt.UserServiceCoroutineStub,
    private val organizationServiceStub: OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub,
    private val reviewRepository: ReviewRepository,
    private val replyReviewRepository: ReplyReviewRepository,
    private val ratingConfigRepository: RatingConfigRepository,
    private val ratingRepository: RatingRepository
) : ReviewService {
    override suspend fun writeReview(request: WriteReviewRequest): Review {
        ensureReviewDoesNotExist(request, request.userId, request.organizationSubsidiaryId)

        val user = userServiceStub.getUserById(
            getUserByIdRequest { id = request.userId }
        )
        val organizationSubsidiary = organizationServiceStub.getOrganizationSubsidiaryById(
            getOrganizationSubsidiaryRequest { id = request.organizationSubsidiaryId }
        )

        val reviewEntity =
            DtoTransformer.transformWriteReviewRequestToReviewEntity(request, user, organizationSubsidiary)
        val savedReview = reviewRepository.save(reviewEntity)

        saveOrUpdateOrganizationRating(savedReview)

        return DtoTransformer.transformReviewEntityToReviewDto(savedReview)
    }

    override suspend fun getOrganizationSubsidiaryRating(request: GetOrganizationSubsidiaryRatingRequest): Rating {
        val retrievedRating = ratingRepository.findByOrganizationSubsidiaryId(request.organizationSubsidiaryId)
        retrievedRating?.let {
            return DtoTransformer.transformRatingEntityToRatingDto(it)
        } ?: throw OrganizationSubsidiaryRatingDoesNotExistException("Organization subsidiary rating does not exist")
    }

    override suspend fun updateReview(request: UpdateReviewRequest): Review {
        val reviewEntity = reviewRepository.findByIdOrNull(request.id)

        reviewEntity?.let {
            DtoTransformer.buildReviewEntityFromReviewDto(it, request)
            val savedReviewEntity = reviewRepository.save(it)
            return DtoTransformer.transformReviewEntityToReviewDto(savedReviewEntity)
        } ?: throw ReviewDoesNotExistException("Review does not exist")
    }

    override suspend fun getOrganizationReviews(request: GetOrganizationReviewsRequest): GetReviewsResponse {
        val pageable = PageRequest.of(request.pagination.page, request.pagination.limitPerPage)
        val retrievedReviews = reviewRepository.findAllByOrganizationId(request.organizationId, pageable)

        return DtoTransformer.transformReviewEntityListToReviewDtoList(retrievedReviews, request.pagination)
    }

    override suspend fun getOrganizationSubsidiaryReviews(request: GetOrganizationSubsidiaryReviewsRequest): GetReviewsResponse {
        val pageable = PageRequest.of(request.pagination.page, request.pagination.limitPerPage)
        val retrievedReviews = reviewRepository.findAllByOrganizationSubsidiaryId(
            request.organizationSubsidiaryId, pageable
        )

        return DtoTransformer.transformReviewEntityListToReviewDtoList(retrievedReviews, request.pagination)
    }

    override suspend fun markReviewAsHelpful(request: MarkReviewAsHelpfulRequest): Empty {
        val retrievedReview = reviewRepository.findByIdOrNull(request.reviewId)
        retrievedReview?.let {
            it.helpfulCount = +1
            reviewRepository.save(it)
            return Empty.getDefaultInstance()
        } ?: throw ReviewDoesNotExistException("Review does not exist")
    }

    override suspend fun replyReview(request: ReplyReviewRequest): ReviewReply {
        val user = userServiceStub.getUserById(
            getUserByIdRequest { id = request.userId }
        )

        val reviewEntity = reviewRepository.findByIdOrNull(request.reviewId)
        reviewEntity?.let {
            val replyReviewEntity = DtoTransformer.transformReplyReviewRequestToReplyReviewEntity(request, user, it)
            val savedReplyReviewEntity = replyReviewRepository.save(replyReviewEntity)

            return DtoTransformer.transformReplyReviewEntityToReplyReviewDto(savedReplyReviewEntity)
        } ?: throw ReviewDoesNotExistException("Review does not exist")
    }

    override suspend fun deleteReview(request: DeleteReviewRequest): Empty {
        reviewRepository.deleteById(request.id)
        return Empty.getDefaultInstance()
    }

    override suspend fun createRatingConfig(request: CreateRatingConfigRequest): RatingConfig {
        val ratingConfigEntity =
            DtoTransformer.transformCreateRatingConfigRequestToRatingConfigEntity(request)
        val savedRatingConfigEntity = ratingConfigRepository.save(ratingConfigEntity)
        return DtoTransformer.transformReviewConfigEntityToReviewConfigDto(savedRatingConfigEntity)
    }

    override suspend fun getRatingConfig(request: GetRatingConfigRequest): RatingConfig {
        val ratingType = request.ratingType
        val ratingConfigEntity = ratingConfigRepository.findByRatingType(ratingType)
        return DtoTransformer.transformRatingConfigEntityToRatingConfigDto(ratingConfigEntity)
    }

    override suspend fun updateRatingConfig(request: UpdateRatingConfigRequest): RatingConfig {
        val ratingConfigEntity = ratingConfigRepository.findById(request.id)
        ratingConfigEntity?.let {
            DtoTransformer.transformRatingConfigDtoToRatingConfigEntity(request, ratingConfigEntity)

            val savedRatingConfigEntity = ratingConfigRepository.save(ratingConfigEntity)

            return DtoTransformer.transformRatingConfigEntityToRatingConfigDto(savedRatingConfigEntity)
        } ?: throw RatingConfigDoesNotExistException("Rating config does not exist")
    }

    private fun saveOrUpdateOrganizationRating(reviewEntity: ReviewEntity) {
        val ratingEntity =
            ratingRepository.findByOrganizationSubsidiaryId(reviewEntity.organizationSubsidiaryId) ?: RatingEntity(
                organizationSubsidiaryId = reviewEntity.organizationSubsidiaryId,
                rating = 0,
                verifiedRatingCount = 0,
                unverifiedRatingCount = 0
            )

        // TODO rating should be decimal not int
        ratingEntity.rating = RatingCalculator.recomputeOrganizationSubsidiaryRating(
            ratingEntity,
            reviewEntity.rating,
            reviewEntity.verified
        )

        if (reviewEntity.verified) {
            ratingEntity.verifiedRatingCount += 1
        } else {
            ratingEntity.unverifiedRatingCount += 1
        }

        ratingRepository.save(ratingEntity)
    }

    private fun ensureReviewDoesNotExist(
        request: WriteReviewRequest,
        userId: Long,
        organizationSubsidiaryId: Long
    ) {
        val reviewExist: Boolean = if (request.anonymous) {
            val hashValue = StringEncoder.hashValue("$userId")
            reviewRepository.existsByUserIdAndOrganizationSubsidiaryId(hashValue, organizationSubsidiaryId)
        } else {
            reviewRepository.existsByUserIdAndOrganizationSubsidiaryId(userId.toString(), organizationSubsidiaryId)
        }

        if (reviewExist) {
            throw CannotWriteReviewException("You cannot write multiple reviews for an organization")
        }
    }
}
