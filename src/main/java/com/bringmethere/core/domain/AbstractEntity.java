package com.bringmethere.core.domain;

import org.springframework.hateoas.Identifiable;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity extends AbstractPersistable<Long> implements Identifiable<Long> {
}