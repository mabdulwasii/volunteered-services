package org.volunteered.apps.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("rating.default")
@ConstructorBinding
data class RatingConfigProperties(
    val unverifiedRatingCount: Double,
    val verifiedRatingCount: Double,
    val currentVerifiedRatingCount: Double,
    val countIncrement: Double,
    )
