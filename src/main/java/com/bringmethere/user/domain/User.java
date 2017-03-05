package com.bringmethere.user.domain;

import com.bringmethere.core.domain.acls.AclImpl;
import com.bringmethere.user.AuthenticationUtils;
import com.bringmethere.user.GrantedAuthorities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Data
@EqualsAndHashCode(of = "username")
@ToString(of = "username")
@Entity
@Inheritance
@DiscriminatorValue("USER")
public class User extends AbstractSecurityUser implements OAuth2User {

    @Column(unique = true, nullable = false)
    private final String username;

    @NotEmpty
    private String name;

    @Email
    private String email;

    @URL
    private String avatarUrl;

    @Transient
    public String getRole() {
        return GrantedAuthorities.ROLE_USER;
    }

    @JsonIgnore
    @Transient
    public List<Sid> getSids(Authentication authentication) {
        List<Sid> sids = new ArrayList<>();

        if (this.equals(AuthenticationUtils.getUser(authentication))) {
            sids.add(new GrantedAuthoritySid(GrantedAuthorities.ENTITY_OWNER));
        }

        return sids;
    }

    @JsonIgnore
    @Transient
    public Acl getAcl() {
        MutableAcl acl = new AclImpl();
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, new GrantedAuthoritySid(GrantedAuthorities.ROLE_ANONYMOUS), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.CREATE, new GrantedAuthoritySid(GrantedAuthorities.ROLE_ANONYMOUS), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, new GrantedAuthoritySid(GrantedAuthorities.ENTITY_OWNER), true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, new GrantedAuthoritySid(GrantedAuthorities.ENTITY_OWNER), true);
        return acl;
    }
}