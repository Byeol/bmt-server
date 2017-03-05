package com.bringmethere.user.domain;

import com.bringmethere.core.domain.AbstractAuditingEntity;
import com.bringmethere.user.AuthenticationUtils;
import com.bringmethere.user.GrantedAuthorities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAuditingUserEntity<PK extends Serializable> extends AbstractAuditingEntity<User, PK> {

    @JsonIgnore
    @Transient
    public boolean isCreatedBy(Authentication authentication) {
        return isCreatedBy(AuthenticationUtils.getUser(authentication));
    }

    @JsonIgnore
    @Transient
    public List<Sid> getSids(Authentication authentication) {
        List<Sid> sids = new ArrayList<>();

        if (isCreatedBy(authentication)) {
            sids.add(new GrantedAuthoritySid(GrantedAuthorities.ENTITY_OWNER));
        }

        return sids;
    }
}