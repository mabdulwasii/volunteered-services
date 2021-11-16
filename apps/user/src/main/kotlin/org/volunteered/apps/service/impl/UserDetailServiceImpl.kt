package org.volunteered.apps.service.impl

import org.springframework.stereotype.Service
import org.volunteered.apps.entity.UserDetails
import org.volunteered.apps.repository.UserDetailsRepository
import org.volunteered.apps.service.UserDetailService

@Service
class UserDetailServiceImpl(private val userDetailsRepository: UserDetailsRepository) : UserDetailService {

    override fun findByEmail(email: String): UserDetails? {
        return userDetailsRepository.findByEmail(email)
    }

    override fun save(userDetails: UserDetails): UserDetails? {
        return userDetailsRepository.save(userDetails);
    }
}