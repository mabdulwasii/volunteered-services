package org.volunteered.apps.review.util

import org.springframework.context.annotation.Configuration
import org.volunteered.apps.review.config.RatingConfigProperties
import org.volunteered.apps.review.entity.RatingEntity

@Configuration
class RatingCalculator(ratingConfigProperties: RatingConfigProperties) {
    private val UNVERIFIED_RATING_WEIGHT = ratingConfigProperties.unverifiedRating
    private val VERIFIED_RATING_WEIGHT = ratingConfigProperties.verifiedRatingWeight

    fun recomputeOrganizationSubsidiaryRating(
        orgSubsidiaryRating: RatingEntity,
        newRating: Int,
        isNewRatingVerified: Boolean
    ): Double {
        val newRatingWeight = if (isNewRatingVerified) VERIFIED_RATING_WEIGHT else UNVERIFIED_RATING_WEIGHT

        val numerator =
            (orgSubsidiaryRating.unverifiedRatingCount * UNVERIFIED_RATING_WEIGHT * orgSubsidiaryRating.rating)
                .plus(orgSubsidiaryRating.verifiedRatingCount * VERIFIED_RATING_WEIGHT * orgSubsidiaryRating.rating)
                .plus(newRating * newRatingWeight)

        val denominator = (orgSubsidiaryRating.unverifiedRatingCount * UNVERIFIED_RATING_WEIGHT)
            .plus(orgSubsidiaryRating.verifiedRatingCount * VERIFIED_RATING_WEIGHT)
            .plus(1)

        return numerator / denominator
    }

}
