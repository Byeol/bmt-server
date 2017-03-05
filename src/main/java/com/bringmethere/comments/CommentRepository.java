package com.bringmethere.comments;

import com.bringmethere.core.repository.AuditingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = CommentExcerpt.class)
public interface CommentRepository extends AuditingRepository<Comment, Long> {
}