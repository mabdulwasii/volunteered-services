package org.volunteered.apps.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.volunteered.apps.auth.model.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
