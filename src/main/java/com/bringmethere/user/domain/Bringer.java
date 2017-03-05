package com.bringmethere.user.domain;

import com.bringmethere.user.GrantedAuthorities;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@NoArgsConstructor(force = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("BRINGER")
public class Bringer extends User {

    public Bringer(String username) {
        super(username);
    }

    private String nickname;

    private String introduction;

    @URL
    private String coverImage;

    private String location;

    private String language;

    @Transient
    public String getRole() {
        return GrantedAuthorities.ROLE_BRINGER;
    }
}