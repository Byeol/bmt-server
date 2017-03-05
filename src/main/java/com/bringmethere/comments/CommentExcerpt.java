package com.bringmethere.comments;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "excerpt", types = Comment.class)
public interface CommentExcerpt {

    Long getId();

    String getBody();
}