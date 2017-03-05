package com.bringmethere.orders;

import com.bringmethere.core.repository.AuditingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "orders", excerptProjection = OrderExcerpt.class)
public interface OrderRepository extends AuditingRepository<ProductOrder, Long> {
}