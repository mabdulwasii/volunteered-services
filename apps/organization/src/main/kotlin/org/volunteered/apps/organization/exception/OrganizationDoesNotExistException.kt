package org.volunteered.apps.organization.exception

class OrganizationDoesNotExistException(override val message: String) : RuntimeException(message)
