package org.volunteered.apps.review.grpc

import com.google.protobuf.Empty
import net.devh.boot.grpc.server.service.GrpcService
import org.volunteered.apps.review.service.ReviewService
import org.volunteered.libs.proto.review.v1.DeleteReviewRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationReviewsRequest
import org.volunteered.libs.proto.review.v1.GetOrganizationSubsidiaryReviewsRequest
import org.volunteered.libs.proto.review.v1.GetReviewsResponse
import org.volunteered.libs.proto.review.v1.MarkReviewAsHelpfulRequest
import org.volunteered.libs.proto.review.v1.ReplyReviewRequest
import org.volunteered.libs.proto.review.v1.Review
import org.volunteered.libs.proto.review.v1.ReviewReply
import org.volunteered.libs.proto.review.v1.ReviewServiceGrpcKt
import org.volunteered.libs.proto.review.v1.WriteReviewRequest

@GrpcService
class ReviewGrpcService(
    private val reviewService: ReviewService
) : ReviewServiceGrpcKt.ReviewServiceCoroutineImplBase() {
    override suspend fun writeReview(request: WriteReviewRequest): Review {
        return reviewService.writeReview(request)
    }

    override suspend fun getOrganizationReviews(request: GetOrganizationReviewsRequest): GetReviewsResponse {
        return reviewService.getOrganizationReviews(request)
    }

    override suspend fun getOrganizationSubsidiaryReviews(request: GetOrganizationSubsidiaryReviewsRequest): GetReviewsResponse {
        return reviewService.getOrganizationSubsidiaryReviews(request)
    }

    override suspend fun markReviewAsHelpful(request: MarkReviewAsHelpfulRequest): Empty {
        return reviewService.markReviewAsHelpful(request)
    }

    override suspend fun replyReview(request: ReplyReviewRequest): ReviewReply {
        return reviewService.replyReview(request)
    }

    override suspend fun deleteReview(request: DeleteReviewRequest): Empty {
        return reviewService.deleteReview(request)
    }
}