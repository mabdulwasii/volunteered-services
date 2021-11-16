package org.volunteered.apps.service

import org.volunteered.apps.entity.UserDetails

interface UserDetailService {
    fun findByEmail(email: String): UserDetails?
    fun save(userDetails: UserDetails): UserDetails?
}