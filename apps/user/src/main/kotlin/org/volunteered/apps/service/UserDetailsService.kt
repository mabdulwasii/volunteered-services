package org.volunteered.apps.service

import org.volunteered.apps.entity.UserDetails

interface UserDetailsService {
    fun findByEmail(email: String): UserDetails?
    fun save(userDetails: UserDetails): UserDetails?
    fun existsByEmail(email: String): Boolean
    fun existsById(id: Long): Boolean
    fun deleteById(id: Long)
    fun findById(id: Long): UserDetails?
}


