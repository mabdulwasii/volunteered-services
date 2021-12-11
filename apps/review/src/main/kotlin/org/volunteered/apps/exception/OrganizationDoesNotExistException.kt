package org.volunteered.apps.exception

class OrganizationDoesNotExistException(override val message: String) : RuntimeException(message)
