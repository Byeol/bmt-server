package com.bringmethere.reviews;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "excerpt", types = Review.class)
public interface ReviewExcerpt {

    Long getId();

    String getTitle();

    String getContent();
}