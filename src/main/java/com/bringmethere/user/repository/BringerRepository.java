package com.bringmethere.user.repository;

import com.bringmethere.user.domain.Bringer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BringerRepository extends CrudRepository<Bringer, Long> {

    Optional<Bringer> findByUsername(@Param("username") String username);
}