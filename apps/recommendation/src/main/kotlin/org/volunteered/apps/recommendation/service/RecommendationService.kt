package org.volunteered.apps.recommendation.service

import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationRequestsResponse
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.GetRecommendationsResponse
import org.volunteered.libs.proto.recommendation.v1.GetUserRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.OrganizationRecommendationRequests
import org.volunteered.libs.proto.recommendation.v1.Recommendation
import org.volunteered.libs.proto.recommendation.v1.RecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.RequestRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.WriteRecommendationRequest

interface RecommendationService {
    suspend fun requestRecommendation(request: RequestRecommendationRequest): RecommendationRequest
    suspend fun getOrganizationRecommendationRequests(request: OrganizationRecommendationRequests): GetOrganizationRecommendationRequestsResponse
    suspend fun writeRecommendation(request: WriteRecommendationRequest): Recommendation
    suspend fun getOrganizationRecommendations(request: GetOrganizationRecommendationsRequest): GetRecommendationsResponse
    suspend fun getUserRecommendations(request: GetUserRecommendationsRequest): GetRecommendationsResponse
}