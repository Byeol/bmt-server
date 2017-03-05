package com.bringmethere.products;

import com.bringmethere.core.domain.acls.AclImpl;
import com.bringmethere.orders.ProductOrder;
import com.bringmethere.products.domain.AbstractProductEntity;
import com.bringmethere.reviews.Review;
import com.bringmethere.user.AuthenticationUtils;
import com.bringmethere.user.GrantedAuthorities;
import com.bringmethere.user.domain.User;
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
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(force = true)
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@Entity
public class Product extends AbstractProductEntity {

    @NotEmpty
    private String name;

    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private final List<Review> reviews = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private final List<ProductOrder> orders = new ArrayList<>();

    @Transient
    @JsonIgnore
    public List<User> getCustomers() {
        return getOrders().stream()
                .filter(ProductOrder::isApproved)
                .map(ProductOrder::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transient
    public boolean isCustomer(Authentication authentication) {
        return getCustomers().contains(AuthenticationUtils.getUser(authentication));
    }

    @Transient
    public boolean isBringer(Authentication authentication) {
        return Objects.equals(getCreatedBy(), AuthenticationUtils.getUser(authentication));
    }

    public void addOrder(ProductOrder order) {
        this.orders.add(order);
        order.setProduct(this);
    }

    @JsonIgnore
    @Transient
    public Acl getAcl() {
        MutableAcl acl = new AclImpl();
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, new GrantedAuthoritySid(GrantedAuthorities.ROLE_ANONYMOUS), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.CREATE, new GrantedAuthoritySid(GrantedAuthorities.ROLE_BRINGER), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, new GrantedAuthoritySid(GrantedAuthorities.ENTITY_OWNER), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, new GrantedAuthoritySid(GrantedAuthorities.ROLE_ADMIN), true);
        return acl;
    }
}