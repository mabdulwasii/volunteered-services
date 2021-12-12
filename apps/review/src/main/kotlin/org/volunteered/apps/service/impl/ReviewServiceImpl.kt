package org.volunteered.apps.service.impl

import com.google.protobuf.Empty
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.volunteered.apps.repository.ReviewRepository
import org.volunteered.apps.service.ReviewService
import org.volunteered.apps.util.DtoTransformer
import org.volunteered.libs.proto.common.v1.OrganizationSubsidiary
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.common.v1.paginationResponse
import org.volunteered.libs.proto.organization.v1.OrganizationServiceGrpcKt
import org.volunteered.libs.proto.organization.v1.getOrganizationSubsidiaryRequest
import org.volunteered.libs.proto.review.v1.DeleteReviewRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationReviewsRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationSubsidiaryReviewsRequest
import org.volunteered.libs.proto.review.v1.GetReviewsResponse
import org.volunteered.libs.proto.review.v1.MarkReviewAsHelpfulRequest
import org.volunteered.libs.proto.review.v1.ReplyReviewRequest
import org.volunteered.libs.proto.review.v1.Review
import org.volunteered.libs.proto.review.v1.ReviewReply
import org.volunteered.libs.proto.review.v1.WriteReviewRequest
import org.volunteered.libs.proto.review.v1.getReviewsResponse
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt
import org.volunteered.libs.proto.user.v1.getUserByIdRequest

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class ReviewServiceImpl(
    private val userServiceStub: UserServiceGrpcKt.UserServiceCoroutineStub,
    private val organizationServiceStub: OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub,
    private val reviewRepository: ReviewRepository
) : ReviewService {
    override suspend fun writeReview(request: WriteReviewRequest): Review {
        val user = getUserById(request.userId)
        val organizationSubsidiary = getOrganizationSubsidiaryById(request.organizationSubsidiaryId)
        val reviewEntity = DtoTransformer.transformWriteReviewRequestToReviewEntity(request, user,
            organizationSubsidiary)

        val savedReview = reviewRepository.save(reviewEntity)

        return DtoTransformer.transformReviewEntityToReviewDto(savedReview)
    }

    override suspend fun getOrganizationReviews(request: GetOrganizationReviewsRequest): GetReviewsResponse {
//        val organizationId = request.organizationId
//        val pagination = request.pagination
//        val limitPerPage = pagination.limitPerPage
//        pagination.page
//
//        val findAllByOrOrganizationSubsidiaryId =
//            reviewRepository.findAllByOrOrganizationSubsidiaryId(organizationId)
        TODO("Not yet implemented")
    }

    override suspend fun getOrganizationSubsidiaryReviews(request: GetOrganizationSubsidiaryReviewsRequest): GetReviewsResponse {
        val organizationSubsidiaryId = request.organizationSubsidiaryId
        val pagination = request.pagination
        val paginationLimitPerPage = pagination.limitPerPage
        val paginationPage = pagination.page

        val pageable = PageRequest.of(paginationPage, paginationLimitPerPage)

        val retrievedReviews =
            reviewRepository.findAllByOrganizationSubsidiaryId(organizationSubsidiaryId, pageable)

        val paginationResponse = paginationResponse {
            total = retrievedReviews.totalElements
            limitPerPage = paginationLimitPerPage
            page = paginationPage
        }
        val reviewsDtoList = DtoTransformer.transformReviewEntityListToReviewDtoList(retrievedReviews.content)
        return getReviewsResponse {
            this.reviews.addAll(reviewsDtoList)
            this.pagination = paginationResponse
        }
    }

    override suspend fun markReviewAsHelpful(request: MarkReviewAsHelpfulRequest): Empty {
        TODO("Not yet implemented")
    }

    override suspend fun replyReview(request: ReplyReviewRequest): ReviewReply {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReview(request: DeleteReviewRequest): Empty {
        TODO("Not yet implemented")
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
}