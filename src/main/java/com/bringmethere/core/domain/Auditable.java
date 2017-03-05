package com.bringmethere.core.domain;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface Auditable<U, ID extends Serializable> extends Persistable<ID> {

    U getCreatedBy();

    void setCreatedBy(final U createdBy);

    LocalDateTime getCreatedDate();

    void setCreatedDate(final LocalDateTime creationDate);

    U getLastModifiedBy();

    void setLastModifiedBy(final U lastModifiedBy);

    LocalDateTime getLastModifiedDate();

    void setLastModifiedDate(final LocalDateTime lastModifiedDate);
}