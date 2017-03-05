package com.bringmethere.products;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "excerpt", types = Product.class)
public interface ProductExcerpt {

    Long getId();

    String getName();

    String getDescription();
}