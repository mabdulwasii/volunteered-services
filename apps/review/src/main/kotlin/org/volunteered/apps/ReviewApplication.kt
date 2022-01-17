package org.volunteered.apps

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ReviewApplication

fun main(args: Array<String>) {
    runApplication<ReviewApplication>(*args)
}