package org.volunteered.apps.review.config

import net.devh.boot.grpc.client.inject.GrpcClient
import net.devh.boot.grpc.client.inject.GrpcClientBean
import net.devh.boot.grpc.client.inject.GrpcClientBeans
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.volunteered.apps.review.repository.OrganizationSubsidiaryRatingRepository
import org.volunteered.apps.review.repository.ReplyReviewRepository
import org.volunteered.apps.review.repository.ReviewRepository
import org.volunteered.apps.review.repository.UserHelpfulReviewRepository
import org.volunteered.apps.review.service.ReviewService
import org.volunteered.apps.review.service.impl.ReviewServiceImpl
import org.volunteered.apps.review.util.RatingCalculator
import org.volunteered.libs.proto.organization.v1.OrganizationServiceGrpcKt
import org.volunteered.libs.proto.user.v1.UserServiceGrpcKt

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@GrpcClientBeans(
    value = [
        GrpcClientBean(
            clazz = UserServiceGrpcKt.UserServiceCoroutineStub::class,
            beanName = "userServiceCoroutineStub",
            client = GrpcClient("user-service")
        ),
        GrpcClientBean(
            clazz = OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub::class,
            beanName = "organizationServiceCoroutineStub",
            client = GrpcClient("organization-service")
        )
    ]
)
class ReviewServiceConfiguration {
    @Bean
    fun reviewService(
        @Autowired userServiceCoroutineStub: UserServiceGrpcKt.UserServiceCoroutineStub,
        @Autowired organizationServiceCoroutineStub: OrganizationServiceGrpcKt.OrganizationServiceCoroutineStub,
        @Autowired reviewRepository: ReviewRepository,
        @Autowired replyReviewRepository: ReplyReviewRepository,
        @Autowired organizationSubsidiaryRatingRepository: OrganizationSubsidiaryRatingRepository,
        @Autowired ratingCalculator: RatingCalculator,
        @Autowired helpfulReviewRepository: UserHelpfulReviewRepository
    ): ReviewService {
        return ReviewServiceImpl(
            userServiceCoroutineStub,
            organizationServiceCoroutineStub,
            reviewRepository,
            replyReviewRepository,
            organizationSubsidiaryRatingRepository,
            ratingCalculator,
            helpfulReviewRepository
        )
    }
}
