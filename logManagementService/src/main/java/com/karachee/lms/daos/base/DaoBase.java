package com.karachee.lms.daos.base;

import com.karachee.lms.repository.base.ExtendedCrudRepository;
import com.karachee.lms.models.Plural;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.stream.Collectors;

public abstract class DaoBase<I, E> {

    public Plural<E> get(Specification<I> specification, Boolean countQuery, Pageable pageable) {
        ExtendedCrudRepository repository = getRepository();
        Plural<E> plural = null;
        if (pageable != null && repository!=null) {
            plural = new Plural<E>(pageable.getOffset(), pageable.getPageSize());
            Page<I> internals = repository.findAll(specification, pageable, countQuery);

            if (internals != null) {
                if (BooleanUtils.isTrue(countQuery)) {
                    plural.setTotalCount(internals.getTotalElements());
                }
                plural.setItems(internals.getContent().stream().map(x -> buildFromDtoWrapper(x)).collect(Collectors.toList()));
            }
        }

        return plural;
    }

    public Plural<E> get(Boolean countQuery, Pageable pageable) {
        ExtendedCrudRepository repository = getRepository();
        Plural<E> plural = null;
        if (pageable != null && repository!=null) {
            plural = new Plural<E>(pageable.getOffset(), pageable.getPageSize());
            Page<I> internals = repository.findAll(null, pageable, countQuery);

            if (internals != null) {
                if (BooleanUtils.isTrue(countQuery)) {
                    plural.setTotalCount(internals.getTotalElements());
                }
                plural.setItems(internals.getContent().stream().map(x -> buildFromDtoWrapper(x)).collect(Collectors.toList()));
            }
        }

        return plural;
    }

    public abstract E buildFromDtoWrapper(I internal);

    public abstract ExtendedCrudRepository getRepository();
}
