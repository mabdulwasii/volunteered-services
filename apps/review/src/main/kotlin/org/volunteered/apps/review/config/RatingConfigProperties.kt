package org.volunteered.apps.review.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("rating.weight")
@ConstructorBinding
data class RatingConfigProperties(
    val unverifiedRatingWeight: Int,
    val verifiedRatingWeight: Int
)
