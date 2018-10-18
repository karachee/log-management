package com.karachee.lms.repository.specifications;

import com.karachee.lms.models.SearchCriteria;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class GenericSpecification<T> implements Specification<T> {

    private SearchCriteria searchCriteria;

    public GenericSpecification(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = null;

        if (searchCriteria != null && StringUtils.isNotBlank(searchCriteria.getColumn()) && searchCriteria.getValue() != null) {
            String operation = (StringUtils.isNotBlank(searchCriteria.getOperation())) ? searchCriteria.getOperation() : "=";

            predicate = (operation.equals("!=")) ? criteriaBuilder.notEqual(root.get(searchCriteria.getColumn()), searchCriteria.getValue().toString())
                            : (operation.equals("=")) ? criteriaBuilder.equal(root.get(searchCriteria.getColumn()), searchCriteria.getValue().toString())
                            :(operation.equals("LIKE")) ? criteriaBuilder.like(root.get(searchCriteria.getColumn()), searchCriteria.getValue().toString())
                            : (operation.equals(">=")) ?
                            (searchCriteria.getValue() instanceof Date)
                                    ? criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getColumn()), (Date) searchCriteria.getValue())
                                    : criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getColumn()), searchCriteria.getValue().toString())
                            : (operation.equals("<=")) ?
                            (searchCriteria.getValue() instanceof Date)
                                    ? criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getColumn()), (Date) searchCriteria.getValue())
                                    : criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getColumn()), searchCriteria.getValue().toString())
                            : null;
        }

        return predicate;
    }
}
