package com.bringmethere.core.security;

import com.bringmethere.core.domain.AuditingEntity;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.ObjectIdentityRetrievalStrategyImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Log
public class AuditingEntityPermissionEvaluator implements PermissionEvaluator {

    @Setter
    private ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy = new ObjectIdentityRetrievalStrategyImpl();
    @Setter
    private ObjectIdentityGenerator objectIdentityGenerator = new ObjectIdentityRetrievalStrategyImpl();
    @Setter
    private SidRetrievalStrategy sidRetrievalStrategy; // = new SidRetrievalStrategyImpl();
    @Setter
    private PermissionFactory permissionFactory = new DefaultPermissionFactory();

    public boolean hasPermission(Authentication authentication, Object domainObject, Object permission) {
        if (domainObject == null) {
            return false;
        }

        if (!(domainObject instanceof AuditingEntity)) {
            return false;
        }

        return checkPermission(authentication, (AuditingEntity) domainObject, permission);
    }

    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

    private boolean checkPermission(Authentication authentication, AuditingEntity auditingEntity, Object permission) {
        if (auditingEntity.isNew()) {
            permission = "create";
        }

        List<Sid> sids = sidRetrievalStrategy.getSids(authentication);
        sids.addAll(auditingEntity.getSids(authentication));

        List<Permission> requiredPermission = resolvePermission(permission);

        logger.info("Checking permission '" + permission + "' for object '" + auditingEntity + "'");
        logger.info(sids.toString());

        try {
            Acl acl = auditingEntity.getAcl();

            if (acl.isGranted(requiredPermission, sids, false)) {
                logger.info("Access is granted");
                return true;
            }

            logger.info("Returning false - ACLs returned, but insufficient permissions for this principal");
        } catch (NotFoundException nfe) {
            logger.info("Returning false - no ACLs apply for this principal");
        }

        return false;

    }

    List<Permission> resolvePermission(Object permission) {
        if (permission instanceof Integer) {
            return Arrays.asList(permissionFactory.buildFromMask((Integer) permission));
        }

        if (permission instanceof Permission) {
            return Arrays.asList((Permission) permission);
        }

        if (permission instanceof Permission[]) {
            return Arrays.asList((Permission[]) permission);
        }

        if (permission instanceof String) {
            String permString = (String) permission;
            Permission p;

            try {
                p = permissionFactory.buildFromName(permString);
            } catch (IllegalArgumentException notfound) {
                p = permissionFactory.buildFromName(permString.toUpperCase(Locale.ENGLISH));
            }

            if (p != null) {
                return Arrays.asList(p);
            }

        }
        throw new IllegalArgumentException("Unsupported permission: " + permission);
    }
}