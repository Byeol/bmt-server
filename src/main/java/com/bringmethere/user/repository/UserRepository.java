package com.bringmethere.user.repository;

import com.bringmethere.core.repository.AuditingRepository;
import com.bringmethere.user.domain.User;
import com.bringmethere.user.domain.UserExcerpt;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RepositoryRestResource(excerptProjection = UserExcerpt.class)
public interface UserRepository extends AuditingRepository<User, Long> {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Optional<User> findByUsername(@Param("username") String username);
}