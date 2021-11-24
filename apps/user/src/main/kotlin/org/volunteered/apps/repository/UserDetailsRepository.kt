package org.volunteered.apps.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.volunteered.apps.entity.UserDetails

@Repository
interface UserDetailsRepository : JpaRepository<UserDetails, Long> {
    fun findByEmail(email: String): UserDetails?
    fun existsByEmail(email: String): Boolean
}

