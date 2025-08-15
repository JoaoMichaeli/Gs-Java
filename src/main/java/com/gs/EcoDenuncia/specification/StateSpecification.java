package com.gs.EcoDenuncia.specification;

import com.gs.EcoDenuncia.controller.StateController;
import com.gs.EcoDenuncia.model.State;
import org.springframework.data.jpa.domain.Specification;

public class StateSpecification {

    public static Specification<State> withFilters(StateController.StateFilters filters) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filters.nome() != null && !filters.nome().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("nome")), "%" + filters.nome().toLowerCase() + "%"));
            }

            if (filters.uf() != null && !filters.uf().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("uf")), "%" + filters.uf().toLowerCase() + "%"));
            }

            return predicates;
        };
    }
}
