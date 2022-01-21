package org.volunteered.apps.recommendation.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.volunteered.apps.recommendation.service.RecommendationService
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationRequestsResponse
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.GetRecommendationsResponse
import org.volunteered.libs.proto.recommendation.v1.GetUserRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.OrganizationRecommendationRequests
import org.volunteered.libs.proto.recommendation.v1.RecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.RecommendationServiceGrpcKt
import org.volunteered.libs.proto.recommendation.v1.RequestRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.WriteRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.WriteRecommendationResponse

@GrpcService
class RecommendationGrpcService(
    private val recommendationService: RecommendationService
) : RecommendationServiceGrpcKt.RecommendationServiceCoroutineImplBase() {
    override suspend fun requestRecommendation(request: RequestRecommendationRequest): RecommendationRequest {
        return recommendationService.requestRecommendation(request)
    }

    override suspend fun getOrganizationRecommendationRequests(request: OrganizationRecommendationRequests): GetOrganizationRecommendationRequestsResponse {
        return recommendationService.getOrganizationRecommendationRequests(request)
    }

    override suspend fun writeRecommendation(request: WriteRecommendationRequest): WriteRecommendationResponse {
        return recommendationService.writeRecommendation(request)
    }

    override suspend fun getOrganizationRecommendations(request: GetOrganizationRecommendationsRequest): GetRecommendationsResponse {
        return recommendationService.getOrganizationRecommendations(request)
    }

    override suspend fun getUserRecommendations(request: GetUserRecommendationsRequest): GetRecommendationsResponse {
        return recommendationService.getUserRecommendations(request)
    }
}
