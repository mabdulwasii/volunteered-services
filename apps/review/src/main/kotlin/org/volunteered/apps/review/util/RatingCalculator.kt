package org.volunteered.apps.review.util

import org.volunteered.apps.review.entity.RatingEntity
import org.volunteered.apps.review.entity.ReviewEntity

class RatingCalculator {
    companion object {
        fun updateRating(
            ratingEntity: RatingEntity,
            verified: Boolean,
            rating: Int,
            verifiedWeight: Int,
            unverifiedWeight: Int,
            newRating: Int
        ): RatingEntity {
            val calculatedRating: Int
            val currentVerifiedRatingCount = ratingEntity.verifiedRatingCount
            val unVerifiedRatingCount = ratingEntity.unverifiedRatingCount

            if (verified) {
                calculatedRating = calculateVerifiedRating(
                    currentVerifiedRatingCount,
                    newRating,
                    unVerifiedRatingCount,
                    rating,
                    verifiedWeight,
                    unverifiedWeight
                )
                ratingEntity.verifiedRatingCount = currentVerifiedRatingCount + 1
                ratingEntity.rating = calculatedRating
            } else {
                calculatedRating = calculateUnVerifiedRating(
                    unVerifiedRatingCount,
                    newRating,
                    currentVerifiedRatingCount,
                    rating,
                    verifiedWeight,
                    unverifiedWeight
                )

                ratingEntity.unverifiedRatingCount = unVerifiedRatingCount + 1
                ratingEntity.rating = calculatedRating
            }

            return ratingEntity
        }

        fun saveRating(
            reviewEntity: ReviewEntity,
            verified: Boolean,
            rating: Int,
            verifiedWeight: Int,
            unverifiedWeight: Int,
            newRating : Int
        ): RatingEntity {
            val unverifiedRatingCount: Int
            val verifiedRatingCount: Int
            val calculatedRating: Int

            if (verified) {
                unverifiedRatingCount = 0
                verifiedRatingCount = 1
                calculatedRating = calculateVerifiedRating(
                    0,
                    newRating,
                    unverifiedRatingCount,
                    rating,
                    verifiedWeight,
                    unverifiedWeight
                )
            } else {
                unverifiedRatingCount = 1
                verifiedRatingCount = 0
                calculatedRating = calculateUnVerifiedRating(
                    0,
                    newRating,
                    verifiedRatingCount,
                    rating,
                    verifiedWeight,
                    unverifiedWeight
                )
            }

            return RatingEntity(
                organizationSubsidiaryId = reviewEntity.organizationSubsidiaryId,
                rating = calculatedRating,
                verifiedRatingCount = verifiedRatingCount,
                unverifiedRatingCount = unverifiedRatingCount
            )
        }

        private fun calculateVerifiedRating(
            currentVerifiedRatingCount: Int,
            newVerifiedRating: Int,
            unVerifiedRatingCount: Int,
            rating: Int,
            verifiedWeight : Int,
            unVerifiedWeight : Int
        ): Int {
            val numerator =
                (currentVerifiedRatingCount * verifiedWeight * rating)
                    .plus (unVerifiedRatingCount * unVerifiedWeight * rating)
                    .plus (newVerifiedRating * verifiedWeight)

            val denominator = (currentVerifiedRatingCount * verifiedWeight)
                .plus(unVerifiedRatingCount * unVerifiedWeight)
                .plus(1)

            return numerator.div(denominator)
        }

        private fun calculateUnVerifiedRating(
            currentUnVerifiedRatingCount: Int,
            newUnVerifiedRating: Int,
            verifiedRatingCount: Int,
            rating: Int,
            verifiedWeight: Int,
            unVerifiedWeight: Int,
        ): Int {
            val numerator =
                (currentUnVerifiedRatingCount * unVerifiedWeight * rating)
                    .plus (verifiedRatingCount * verifiedWeight * rating)
                    .plus (newUnVerifiedRating * unVerifiedWeight)

            val denominator = (currentUnVerifiedRatingCount * unVerifiedWeight)
                .plus(verifiedRatingCount * verifiedWeight)
                .plus(1)

            return numerator.div(denominator)
        }
    }

}