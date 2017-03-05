package com.bringmethere.products.domain;

import org.springframework.security.core.Authentication;

public interface ProductEntity {

    boolean isCustomer(Authentication authentication);

    boolean isBringer(Authentication authentication);
}