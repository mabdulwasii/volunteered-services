package org.volunteered.apps.review.exception

class UserDoesNotExistException(override val message: String) : RuntimeException(message)
