package org.volunteered.apps.user.exception

class UserAlreadyExistsException(override val message: String) : RuntimeException(message)
