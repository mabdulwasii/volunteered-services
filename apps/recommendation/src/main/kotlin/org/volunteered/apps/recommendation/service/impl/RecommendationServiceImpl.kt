package org.volunteered.apps.recommendation.service.impl

import org.springframework.stereotype.Service
import org.volunteered.apps.recommendation.service.RecommendationService
import org.volunteered.libs.proto.organization.v1.OrganizationServiceGrpcKt
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationRequestsResponse
import org.volunteered.libs.proto.recommendation.v1.GetOrganizationRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.GetRecommendationsResponse
import org.volunteered.libs.proto.recommendation.v1.GetUserRecommendationsRequest
import org.volunteered.libs.proto.recommendation.v1.OrganizationRecommendationRequests
import org.volunteered.libs.proto.recommendation.v1.Recommendation
import org.volunteered.libs.proto.recommendation.v1.RecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.RequestRecommendationRequest
import org.volunteered.libs.proto.recommendation.v1.WriteRecommendationRequest
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Service
class RecommendationServiceImpl(
    private val userServiceStub: UserServiceGrpcKt.UserServiceCoroutineStub,
    private val organizationServiceCoroutineStub: OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub,
) : RecommendationService {
    override suspend fun requestRecommendation(request: RequestRecommendationRequest): RecommendationRequest {
        TODO("Not yet implemented")
    }

    override suspend fun getOrganizationRecommendationRequests(request: OrganizationRecommendationRequests): GetOrganizationRecommendationRequestsResponse {
        TODO("Not yet implemented")
    }

    override suspend fun writeRecommendation(request: WriteRecommendationRequest): Recommendation {
        TODO("Not yet implemented")
    }

    override suspend fun getOrganizationRecommendations(request: GetOrganizationRecommendationsRequest): GetRecommendationsResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getUserRecommendations(request: GetUserRecommendationsRequest): GetRecommendationsResponse {
        TODO("Not yet implemented")
    }
}
