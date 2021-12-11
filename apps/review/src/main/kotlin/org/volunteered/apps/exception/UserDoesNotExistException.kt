package org.volunteered.apps.exception

class UserDoesNotExistException(override val message: String) : RuntimeException(message)
