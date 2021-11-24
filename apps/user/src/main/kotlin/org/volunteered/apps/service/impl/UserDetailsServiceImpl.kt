package org.volunteered.apps.service.impl

import org.springframework.stereotype.Service
import org.volunteered.apps.entity.UserDetails
import org.volunteered.apps.repository.UserDetailsRepository

@Service
class UserDetailsServiceImpl(private val userDetailsRepository: UserDetailsRepository) :
    org.volunteered.apps.service.UserDetailsService {

    override fun findByEmail(email: String): UserDetails? {
        return userDetailsRepository.findByEmail(email)
    }

    override fun save(userDetails: UserDetails): UserDetails? {
        return userDetailsRepository.save(userDetails)
    }

    override fun existsByEmail(email: String): Boolean {
        return userDetailsRepository.existsByEmail(email)
    }

    override fun deleteById(id: Int) {
        userDetailsRepository.deleteById(id.toLong())
    }

    override fun existsById(id: Long): Boolean {
        return userDetailsRepository.existsById(id)
    }
}

