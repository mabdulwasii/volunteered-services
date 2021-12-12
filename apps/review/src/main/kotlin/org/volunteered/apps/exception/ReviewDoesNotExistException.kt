package org.volunteered.apps.exception

class ReviewDoesNotExistException(override val message: String) : RuntimeException(message)
