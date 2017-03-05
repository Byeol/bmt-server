package com.bringmethere.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.persistence.Transient;
import java.util.Collection;

public abstract class AbstractSecurityUser extends AbstractAuditingUserEntity<Long> implements SecurityUser {

    @JsonIgnore
    @Transient
    public abstract String getRole();

    @JsonIgnore
    @Transient
    public Collection<GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(getRole());
    }

    @JsonIgnore
    @Transient
    public String getPassword() {
        return "PASSWORD";
    }
}