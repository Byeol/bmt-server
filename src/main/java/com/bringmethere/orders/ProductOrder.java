package com.bringmethere.orders;

import com.bringmethere.core.domain.acls.AclImpl;
import com.bringmethere.products.Product;
import com.bringmethere.products.domain.AbstractProductEntity;
import com.bringmethere.user.AuthenticationUtils;
import com.bringmethere.user.GrantedAuthorities;
import com.bringmethere.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.core.Authentication;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Objects;

@NoArgsConstructor(force = true)
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Entity
public class ProductOrder extends AbstractProductEntity {

    @Transient
    public User getUser() {
        return getCreatedBy();
    }

    @ManyToOne(cascade = CascadeType.REFRESH, optional = false)
    private Product product;

    private Status status = Status.PENDING;

    public static enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    @Transient
    @JsonIgnore
    public boolean isApproved() {
        return ProductOrder.Status.APPROVED.equals(this.status);
    }

    @Transient
    public boolean isCreatedBy(Authentication authentication) {
        return Objects.equals(getCreatedBy(), AuthenticationUtils.getUser(authentication));
    }

    @Transient
    public boolean isCustomer(Authentication authentication) {
        return getProduct().isCustomer(authentication);
    }

    @Transient
    public boolean isBringer(Authentication authentication) {
        return getProduct().isBringer(authentication);
    }

    @JsonIgnore
    @Transient
    public Acl getAcl() {
        MutableAcl acl = new AclImpl();
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, new GrantedAuthoritySid(GrantedAuthorities.ROLE_ANONYMOUS), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.CREATE, new GrantedAuthoritySid(GrantedAuthorities.ROLE_USER), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, new GrantedAuthoritySid(GrantedAuthorities.PRODUCT_BRINGER), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, new GrantedAuthoritySid(GrantedAuthorities.ENTITY_OWNER), true);
        return acl;
    }
}