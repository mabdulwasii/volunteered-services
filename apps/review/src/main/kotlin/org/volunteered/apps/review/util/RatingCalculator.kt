package org.volunteered.apps.review.util

import org.springframework.stereotype.Component
import org.volunteered.apps.review.config.RatingConfigProperties
import org.volunteered.apps.review.entity.OrganizationSubsidiaryRatingEntity

@Component
class RatingCalculator(private val ratingConfigProperties: RatingConfigProperties) {
    fun recomputeOrganizationSubsidiaryRating(
        orgSubsidiaryRating: OrganizationSubsidiaryRatingEntity,
        newRating: Int,
        isNewRatingVerified: Boolean
    ): Double {
        val unverifiedRatingWeight = ratingConfigProperties.unverifiedRatingWeight
        val verifiedRatingWeight = ratingConfigProperties.verifiedRatingWeight
        val newRatingWeight = if (isNewRatingVerified) verifiedRatingWeight else unverifiedRatingWeight

        val numerator =
            (orgSubsidiaryRating.unverifiedRatingCount * unverifiedRatingWeight * orgSubsidiaryRating.rating)
                .plus(orgSubsidiaryRating.verifiedRatingCount * verifiedRatingWeight * orgSubsidiaryRating.rating)
                .plus(newRating * newRatingWeight)

        val denominator = (orgSubsidiaryRating.unverifiedRatingCount * unverifiedRatingWeight)
            .plus(orgSubsidiaryRating.verifiedRatingCount * verifiedRatingWeight)
            .plus(1).toDouble()

        return numerator / denominator
    }
}
