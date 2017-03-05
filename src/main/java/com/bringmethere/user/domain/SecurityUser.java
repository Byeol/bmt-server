package com.bringmethere.user.domain;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface SecurityUser {

    Collection<GrantedAuthority> getAuthorities();

    String getPassword();

    String getUsername();
}