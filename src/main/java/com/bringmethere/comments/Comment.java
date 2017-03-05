package com.bringmethere.comments;

import com.bringmethere.core.domain.acls.AclImpl;
import com.bringmethere.products.domain.AbstractProductEntity;
import com.bringmethere.reviews.Review;
import com.bringmethere.user.GrantedAuthorities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.core.Authentication;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@NoArgsConstructor(force = true)
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Entity
public class Comment extends AbstractProductEntity {

    @NotEmpty
    private String body;

    @ManyToOne(cascade = CascadeType.REFRESH, optional = false)
    private Review review;

    public boolean isCustomer(Authentication authentication) {
        return getReview().isCustomer(authentication);
    }

    public boolean isBringer(Authentication authentication) {
        return getReview().isBringer(authentication);
    }

    @JsonIgnore
    @Transient
    public Acl getAcl() {
        MutableAcl acl = new AclImpl();
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, new GrantedAuthoritySid(GrantedAuthorities.ROLE_ANONYMOUS), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.CREATE, new GrantedAuthoritySid(GrantedAuthorities.ROLE_USER), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, new GrantedAuthoritySid(GrantedAuthorities.ENTITY_OWNER), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, new GrantedAuthoritySid(GrantedAuthorities.ENTITY_OWNER), true);
        return acl;
    }
}