package com.bringmethere.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.hateoas.Identifiable;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity<U, PK extends Serializable> extends AbstractAuditable<U, PK> implements AuditingEntity<U, PK>, Identifiable<PK> {

    @JsonIgnore
    @Transient
    public boolean isCreatedBy(U createdBy) {
        return getCreatedBy() != null && getCreatedBy().equals(createdBy);
    }
}