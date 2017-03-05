package com.bringmethere.products.domain;

import com.bringmethere.core.domain.acls.AclImpl;
import com.bringmethere.user.GrantedAuthorities;
import com.bringmethere.user.domain.AbstractAuditingUserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.List;

@MappedSuperclass
public abstract class AbstractProductEntity extends AbstractAuditingUserEntity<Long> implements ProductEntity {

    @JsonIgnore
    @Transient
    public List<Sid> getSids(Authentication authentication) {
        List<Sid> sids = super.getSids(authentication);

        if (isCustomer(authentication)) {
            sids.add(new GrantedAuthoritySid(GrantedAuthorities.PRODUCT_CUSTOMER));
        }

        if (isBringer(authentication)) {
            sids.add(new GrantedAuthoritySid(GrantedAuthorities.PRODUCT_BRINGER));
        }

        return sids;
    }

    @JsonIgnore
    @Transient
    public Acl getAcl() {
        MutableAcl acl = new AclImpl();
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, new GrantedAuthoritySid(GrantedAuthorities.ROLE_ANONYMOUS), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.CREATE, new GrantedAuthoritySid(GrantedAuthorities.PRODUCT_CUSTOMER), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, new GrantedAuthoritySid(GrantedAuthorities.ENTITY_OWNER), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, new GrantedAuthoritySid(GrantedAuthorities.ENTITY_OWNER), true);
        return acl;
    }
}