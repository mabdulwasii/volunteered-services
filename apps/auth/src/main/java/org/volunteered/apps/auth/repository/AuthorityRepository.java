package org.volunteered.apps.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.volunteered.apps.auth.model.Authority;
import org.volunteered.apps.auth.model.enumeration.AuthorityType;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByName(AuthorityType name);
}
