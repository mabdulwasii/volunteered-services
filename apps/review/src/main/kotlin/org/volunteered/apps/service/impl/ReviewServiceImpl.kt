package org.volunteered.apps.service.impl

import com.google.protobuf.Empty
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.volunteered.apps.entity.ReviewEntity
import org.volunteered.apps.exception.CannotWriteReviewException
import org.volunteered.apps.exception.RatingConfigDoesNotExistException
import org.volunteered.apps.exception.ReviewDoesNotExistException
import org.volunteered.apps.repository.RatingConfigRepository
import org.volunteered.apps.repository.RatingRepository
import org.volunteered.apps.repository.ReplyReviewRepository
import org.volunteered.apps.repository.ReviewRepository
import org.volunteered.apps.service.ReviewService
import org.volunteered.apps.util.DtoTransformer
import org.volunteered.apps.util.RatingCalculator
import org.volunteered.apps.util.StringEncoder
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.organization.v1.OrganizationServiceGrpcKt
import org.volunteered.libs.proto.organization.v1.getOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.review.v1.CreateRatingConfigRequest
import org.volunteered.libs.proto.review.v1.DeleteReviewRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationReviewsRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationSubsidiaryReviewsRequest
import org.volunteered.libs.proto.review.v1.GetReviewsResponse
import org.volunteered.libs.proto.review.v1.MarkReviewAsHelpfulRequest
import org.volunteered.libs.proto.review.v1.RatingConfig
import org.volunteered.libs.proto.review.v1.RatingType
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
    private val replReviewRepository: ReplyReviewRepository,
    private val ratingConfigRepository: RatingConfigRepository,
    private val ratingRepository: RatingRepository
) : ReviewService {
    override suspend fun writeReview(request: WriteReviewRequest): Review {
        val userId = request.userId
        val organizationSubsidiaryId = request.organizationSubsidiaryId

        ensureReviewExist(request, userId, organizationSubsidiaryId)

        val user = getUserById(userId)
        val organizationSubsidiary = getOrganizationSubsidiaryById(organizationSubsidiaryId)

        val reviewEntity = DtoTransformer.transformWriteReviewRequestToReviewEntity(request, user,
            organizationSubsidiary)

        val savedReview = reviewRepository.save(reviewEntity)

        saveOrUpdateOrganizationRating(savedReview)

        return DtoTransformer.transformReviewEntityToReviewDto(savedReview)
    }

    private fun ensureReviewExist(
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

    override suspend fun updateReview(request: UpdateReviewRequest): Review {
        val reviewEntity = reviewRepository.findByIdOrNull(request.id)

        reviewEntity?.let {
            DtoTransformer.buildReviewEntityFromReviewDto(it, request)
            val savedReviewEntity = reviewRepository.save(it)
            return DtoTransformer.transformReviewEntityToReviewDto(savedReviewEntity)
        }?: throw ReviewDoesNotExistException("Review does not exist")
    }

    override suspend fun getOrganizationReviews(request: GetOrganizationReviewsRequest): GetReviewsResponse {
        val organizationId = request.organizationId
        val pagination = request.pagination
        val paginationLimitPerPage = pagination.limitPerPage
        val paginationPage = pagination.page

        val pageable = PageRequest.of(paginationPage, paginationLimitPerPage)

        val retrievedReviews = reviewRepository.findAllByOrganizationId(organizationId, pageable)

        return DtoTransformer.transformReviewEntityListToReviewDtoList(retrievedReviews, pagination)
    }

    override suspend fun getOrganizationSubsidiaryReviews(request: GetOrganizationSubsidiaryReviewsRequest): GetReviewsResponse {
        val organizationSubsidiaryId = request.organizationSubsidiaryId
        val pagination = request.pagination
        val paginationLimitPerPage = pagination.limitPerPage
        val paginationPage = pagination.page

        val pageable = PageRequest.of(paginationPage, paginationLimitPerPage)

        val retrievedReviews =
            reviewRepository.findAllByOrganizationSubsidiaryId(organizationSubsidiaryId, pageable)

        return DtoTransformer.transformReviewEntityListToReviewDtoList(retrievedReviews, pagination)
    }

    override suspend fun markReviewAsHelpful(request: MarkReviewAsHelpfulRequest): Empty {
        val reviewId = request.reviewId
        val retrievedReview = reviewRepository.findByIdOrNull(reviewId)
        retrievedReview?.let {
            it.helpfulCount =+ 1
            reviewRepository.save(it)
            return Empty.getDefaultInstance()
        } ?: throw  ReviewDoesNotExistException("Review does not exist")
    }

    override suspend fun replyReview(request: ReplyReviewRequest): ReviewReply {
        val user = getUserById(request.userId)

        val reviewEntity = reviewRepository.findByIdOrNull(request.reviewId)
        reviewEntity?.let {
            val replyReviewEntity = DtoTransformer.transformReplyReviewRequestToReplyReviewEntity(request, user, it)
            val savedReplyReviewEntity = replReviewRepository.save(replyReviewEntity)

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

    override suspend fun updateRatingConfig(request: UpdateRatingConfigRequest): RatingConfig {
        val ratingConfigEntity = ratingConfigRepository.findById(request.id)
        ratingConfigEntity?.let {
            DtoTransformer.transformRatingConfigDtoToRatingConfigEntity(request, ratingConfigEntity)

            val savedRatingConfigEntity = ratingConfigRepository.save(ratingConfigEntity)

            return DtoTransformer.transformRatingConfigEntityToRatingConfigDto(savedRatingConfigEntity)
        } ?: throw RatingConfigDoesNotExistException("Rating config does not exist")
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

    private fun saveOrUpdateOrganizationRating(reviewEntity: ReviewEntity) {
        val verified = reviewEntity.verified
        val organizationSubsidiaryId = reviewEntity.organizationSubsidiaryId
        val newRating = reviewEntity.rating

        val unverifiedRatingConfig = ratingConfigRepository.findByRatingType(RatingType.UNVERIFIED)
        val verifiedRatingConfig = ratingConfigRepository.findByRatingType(RatingType.VERIFIED)

        var ratingEntity = ratingRepository.findByOrganizationSubsidiaryId(organizationSubsidiaryId)


        if (ratingEntity == null) {
             ratingEntity =
                RatingCalculator.saveRating(reviewEntity, verified, 0, verifiedRatingConfig.weight,
                    unverifiedRatingConfig.weight, newRating)
        } else {
            RatingCalculator.updateRating(ratingEntity, verified, ratingEntity.rating, verifiedRatingConfig.weight,
                unverifiedRatingConfig.weight, newRating)
        }
        ratingRepository.save(ratingEntity)
    }

}