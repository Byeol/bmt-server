package com.bringmethere.config;

import com.bringmethere.core.security.RoleHierarchyUtils;
import com.bringmethere.user.GrantedAuthorities;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class WebConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        final RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(RoleHierarchyUtils.getRoleHierarchyStringRepresentation(GrantedAuthorities.ROLE_ADMIN, GrantedAuthorities.ROLE_BRINGER, GrantedAuthorities.ROLE_USER, GrantedAuthorities.ROLE_ANONYMOUS));
        return roleHierarchy;
    }
}
