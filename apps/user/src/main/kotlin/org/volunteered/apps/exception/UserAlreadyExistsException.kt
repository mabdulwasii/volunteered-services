package org.volunteered.apps.exception

class UserAlreadyExistsException(override val message: String) : RuntimeException(message)
