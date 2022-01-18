package org.volunteered.apps.review.service

import com.google.protobuf.Empty
import org.volunteered.libs.proto.review.v1.DeleteReviewRequest
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

interface ReviewService {
    suspend fun writeReview(request: WriteReviewRequest): Review
    suspend fun getOrganizationSubsidiaryReviews(request: GetOrganizationSubsidiaryReviewsRequest): GetReviewsResponse
    suspend fun getUserReviews(request: GetUserReviewsRequest): GetReviewsResponse
    suspend fun markReviewAsHelpful(request: MarkReviewAsHelpfulRequest): Empty
    suspend fun replyReview(request: ReplyReviewRequest): ReviewReply
    suspend fun deleteReview(request: DeleteReviewRequest): Empty
    suspend fun updateReview(request: UpdateReviewRequest): Review
    suspend fun getOrganizationSubsidiaryRating(request: GetOrganizationSubsidiaryRatingRequest): Rating
    suspend fun getReviewReplies(request: GetReviewRepliesRequest): GetReviewRepliesResponse
}
