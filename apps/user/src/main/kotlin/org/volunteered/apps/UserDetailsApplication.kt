package org.volunteered.apps

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UserDetailsApplication

fun main(args: Array<String>) {
    runApplication<UserDetailsApplication>(*args)
}