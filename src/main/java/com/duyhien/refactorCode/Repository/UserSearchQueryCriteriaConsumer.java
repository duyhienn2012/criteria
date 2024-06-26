package com.duyhien.refactorCode.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchQueryCriteriaConsumer implements Consumer<SearchCriteria> {

    private CriteriaBuilder builder;

    private Predicate predicate;

    private Root root;
    @Override
    public void accept(SearchCriteria searchCriteria) {

        if (searchCriteria.getOperation().equalsIgnoreCase(">")) {
            predicate = builder.and(predicate, builder.greaterThan(root.get(searchCriteria.getKey())
                    , searchCriteria.getValue().toString()));
        } else if (searchCriteria.getOperation().equalsIgnoreCase("<")) {
            predicate = builder.and(predicate, builder.lessThan(root.get(searchCriteria.getKey())
                    , searchCriteria.getValue().toString()));
        } else if (searchCriteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(searchCriteria.getKey()).getJavaType() == String.class) {
                predicate = builder.and(predicate, builder.like(root.get(searchCriteria.getKey())
                        , "%" + searchCriteria.getValue().toString() + "%"));
            } else {
                predicate = builder.and(predicate, builder.equal(
                        root.get(searchCriteria.getKey()), searchCriteria.getValue()));
            }
        }

    }
}
