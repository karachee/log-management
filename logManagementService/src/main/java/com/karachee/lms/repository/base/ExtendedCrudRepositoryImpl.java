package com.karachee.lms.repository.base;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class ExtendedCrudRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ExtendedCrudRepository<T, ID> {

    private EntityManager entityManager;

    public ExtendedCrudRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable, Boolean countQuery) {
        TypedQuery<T> query = this.getQuery(spec, pageable);
        return (pageable == null) ? new PageImpl(query.getResultList())
                : (BooleanUtils.isFalse(countQuery)) ? readPageNoCountQuery(query, this.getDomainClass(), pageable, spec)
                : readPage(query, this.getDomainClass(), pageable, spec);
    }

    protected <S extends T> PageImpl readPageNoCountQuery(TypedQuery<S> query, final Class<S> domainClass, Pageable pageable, final Specification<S> spec) {
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl(query.getResultList(), pageable, -1);
    }

    protected <S extends T> Page<S> readPage(TypedQuery<S> query, final Class<S> domainClass, Pageable pageable, final Specification<S> spec) {
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return PageableExecutionUtils.getPage(query.getResultList(), pageable, new PageableExecutionUtils.TotalSupplier() {
            public long get() {
                return executeCountQuery(getCountQuery(spec, domainClass));
            }
        });
    }

    private static Long executeCountQuery(TypedQuery<Long> query) {
        Assert.notNull(query, "TypedQuery must not be null!");
        List<Long> totals = query.getResultList();
        Long total = 0L;

        Long element;
        for (Iterator<Long> var3 = totals.iterator(); var3.hasNext(); total = total + (element == null ? 0L : element)) {
            element = var3.next();
        }

        return total;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
