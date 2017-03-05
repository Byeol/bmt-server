package com.bringmethere.core.domain.acls;

import lombok.Getter;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.model.*;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AclImpl implements Acl, MutableAcl {

    @Getter
    private Acl parentAcl;
    private transient AclAuthorizationStrategy aclAuthorizationStrategy;
    private transient PermissionGrantingStrategy permissionGrantingStrategy;
    private final List<AccessControlEntry> aces = new ArrayList<>();
    @Getter
    private ObjectIdentity objectIdentity;
    private Serializable id;
    @Getter
    private Sid owner; // OwnershipAcl
    private List<Sid> loadedSids = null;
    private boolean entriesInheriting = true;

    public AclImpl() {
        AuditLogger auditLogger = new ConsoleAuditLogger();
        this.permissionGrantingStrategy = new DefaultPermissionGrantingStrategy(auditLogger);
    }

    public void deleteAce(int aceIndex) throws NotFoundException {

    }

    public Serializable getId() {
        return null;
    }

    public void insertAce(int atIndexLocation, Permission permission, Sid sid, boolean granting) throws NotFoundException {
        Assert.notNull(permission, "Permission required");
        Assert.notNull(sid, "Sid required");

        if (atIndexLocation < 0) {
            throw new NotFoundException("atIndexLocation must be greater than or equal to zero");
        }

        if (atIndexLocation > this.aces.size()) {
            throw new NotFoundException("atIndexLocation must be less than or equal to the size of the AccessControlEntry collection");
        }

        AccessControlEntryImpl ace = new AccessControlEntryImpl(null, this, sid, permission, granting, false, false);

        synchronized (aces) {
            this.aces.add(atIndexLocation, ace);
        }
    }

    public List<AccessControlEntry> getEntries() {
        return new ArrayList<AccessControlEntry>(aces);
    }

    public void setOwner(Sid newOwner) {
        Assert.notNull(newOwner, "Owner required");
        this.owner = newOwner;
    }

    public void setEntriesInheriting(boolean entriesInheriting) {
        this.entriesInheriting = entriesInheriting;
    }

    public void setParent(Acl newParent) {
        Assert.isTrue(newParent == null || !newParent.equals(this), "Cannot be the parent of yourself");
        this.parentAcl = newParent;
    }

    public void updateAce(int aceIndex, Permission permission) throws NotFoundException {
    }

    public boolean isEntriesInheriting() {
        return entriesInheriting;
    }

    public boolean isGranted(List<Permission> permission, List<Sid> sids,
                             boolean administrativeMode) throws NotFoundException, UnloadedSidException {
        Assert.notEmpty(permission, "Permissions required");
        Assert.notEmpty(sids, "SIDs required");

        if (!this.isSidLoaded(sids)) {
            throw new UnloadedSidException("ACL was not loaded for one or more SID");
        }

        return permissionGrantingStrategy.isGranted(this, permission, sids,
                administrativeMode);
    }

    public boolean isSidLoaded(List<Sid> sids) {
        // If loadedSides is null, this indicates all SIDs were loaded
        // Also return true if the caller didn't specify a SID to find
        if ((this.loadedSids == null) || (sids == null) || (sids.size() == 0)) {
            return true;
        }

        // This ACL applies to a SID subset only. Iterate to check it applies.
        for (Sid sid : sids) {
            boolean found = false;

            for (Sid loadedSid : loadedSids) {
                if (sid.equals(loadedSid)) {
                    // this SID is OK
                    found = true;

                    break; // out of loadedSids for loop
                }
            }

            if (!found) {
                return false;
            }
        }

        return true;
    }
}