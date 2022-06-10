package org.volunteered.apps.recommendation.service

import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationRequests
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationRequestsResponse
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.GetRecommendationsResponse
import org.volunteered.libs.proto.recommendation.v1.GetUserRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.RecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.RequestRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.WriteRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.WriteRecommendationResponse

interface RecommendationService {
    suspend fun requestRecommendation(request: RequestRecommendationRequest): RecommendationRequest
    suspend fun getRecommendationRequestsForOrganization(request: GetOrganizationRecommendationRequests): GetOrganizationRecommendationRequestsResponse
    suspend fun writeRecommendation(request: WriteRecommendationRequest): WriteRecommendationResponse
    suspend fun getOrganizationRecommendations(request: GetOrganizationRecommendationsRequest): GetRecommendationsResponse
    suspend fun getUserRecommendations(request: GetUserRecommendationsRequest): GetRecommendationsResponse
}
