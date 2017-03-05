package com.bringmethere.user;

import com.bringmethere.user.domain.Bringer;
import com.bringmethere.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthenticationUtils {

    public static User getUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(GrantedAuthorities.ROLE_USER))) {
            return new User(authentication.getName());
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(GrantedAuthorities.ROLE_BRINGER))) {
            return new Bringer(authentication.getName());
        }

        return null;
    }
}