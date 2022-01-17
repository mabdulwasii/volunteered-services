package org.volunteered.apps.review.util

import org.volunteered.apps.review.entity.RatingEntity

class RatingCalculator {
    companion object {
        // TODO get these values from config. Create a RatingConfig class to get the config values and inject that
        //  class into this one on instantiation. Let an instance of this class be passed into ReviewServiceImpl
        //  Hence, the subsequent functions need not be encapsulated in a companion object
        private val UNVERIFIED_RATING_WEIGHT = 1
        private val VERIFIED_RATING_WEIGHT = 3

        fun recomputeOrganizationSubsidiaryRating(
            orgSubsidiaryRating: RatingEntity,
            newRating: Int,
            isNewRatingVerified: Boolean
        ): Int {
            val newRatingWeight = if (isNewRatingVerified) VERIFIED_RATING_WEIGHT else UNVERIFIED_RATING_WEIGHT

            val numerator =
                (orgSubsidiaryRating.unverifiedRatingCount * UNVERIFIED_RATING_WEIGHT * orgSubsidiaryRating.rating)
                    .plus(orgSubsidiaryRating.verifiedRatingCount * VERIFIED_RATING_WEIGHT * orgSubsidiaryRating.rating)
                    .plus(newRating * newRatingWeight)

            val denominator = (orgSubsidiaryRating.unverifiedRatingCount * UNVERIFIED_RATING_WEIGHT)
                .plus(orgSubsidiaryRating.verifiedRatingCount * VERIFIED_RATING_WEIGHT)
                .plus(1)

            // TODO should not be integer division
            return numerator / denominator
        }
    }
}
