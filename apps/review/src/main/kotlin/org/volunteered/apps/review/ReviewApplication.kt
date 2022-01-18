package org.volunteered.apps.review

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.volunteered.apps.review.config.RatingConfigProperties

@SpringBootApplication
@EnableConfigurationProperties(RatingConfigProperties::class)
class ReviewApplication

fun main(args: Array<String>) {
    runApplication<ReviewApplication>(*args)
}
