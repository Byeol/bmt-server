package com.bringmethere.core.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface AuditingRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {

    @PreAuthorize("hasPermission(#entity, 'write')")
    @Override
    <S extends T> S save(S entity);

    @PreFilter("hasPermission(filterTarget, 'write')")
    @Override
    <S extends T> List<S> save(Iterable<S> entities);

    @PostAuthorize("hasPermission(returnObject, 'read')")
    @Override
    T findOne(ID id);

    @PostAuthorize("hasPermission(returnObject, 'read')")
    @Override
    boolean exists(ID id);

    @PostFilter("hasPermission(filterObject, 'read')")
    @Override
    List<T> findAll();

    @PostFilter("hasPermission(filterObject, 'read')")
    @Override
    List<T> findAll(Iterable<ID> ids);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    long count();

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void delete(ID id);

    @PreAuthorize("hasPermission(#entity, 'delete')")
    @Override
    void delete(T entity);

    @PreFilter("hasPermission(filterTarget, 'delete')")
    @Override
    void delete(Iterable<? extends T> entities);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    void deleteAll();
}