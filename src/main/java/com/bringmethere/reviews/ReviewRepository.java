package com.bringmethere.reviews;

import com.bringmethere.core.repository.AuditingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = ReviewExcerpt.class)
public interface ReviewRepository extends AuditingRepository<Review, Long> {
}