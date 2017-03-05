package com.bringmethere.products;

import com.bringmethere.core.repository.AuditingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = ProductExcerpt.class)
public interface ProductRepository extends AuditingRepository<Product, Long> {
}