package com.bringmethere.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractAuditable<U, PK extends Serializable> extends AbstractPersistable<PK> implements Auditable<U, PK> {

    @Getter
    @Setter
    @ManyToOne
    @CreatedBy
    private U createdBy;

    @Getter
    @Setter
    @CreatedDate
    private LocalDateTime createdDate;

    @Getter
    @Setter
    @ManyToOne
    @LastModifiedBy
    private U lastModifiedBy;

    @Getter
    @Setter
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
