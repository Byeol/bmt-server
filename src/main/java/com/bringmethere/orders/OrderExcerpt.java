package com.bringmethere.orders;

import com.bringmethere.products.ProductExcerpt;
import com.bringmethere.user.domain.UserExcerpt;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "excerpt", types = ProductOrder.class)
public interface OrderExcerpt {

    Long getId();

    UserExcerpt getUser();

    ProductExcerpt getProduct();

    ProductOrder.Status getStatus();
}