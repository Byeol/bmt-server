package com.bringmethere.config;

import com.bringmethere.core.security.AuditingEntityPermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    private RoleHierarchy roleHierarchy;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        final DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        AuditingEntityPermissionEvaluator permissionEvaluator = new AuditingEntityPermissionEvaluator();
        permissionEvaluator.setSidRetrievalStrategy(new SidRetrievalStrategyImpl(this.roleHierarchy));
        handler.setPermissionEvaluator(permissionEvaluator);
        handler.setRoleHierarchy(this.roleHierarchy);

        return handler;
    }
}