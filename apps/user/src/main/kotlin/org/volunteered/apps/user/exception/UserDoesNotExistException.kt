package org.volunteered.apps.user.exception

class UserDoesNotExistException(override val message: String) : RuntimeException(message)
