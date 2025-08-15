package com.gs.EcoDenuncia.specification;

import com.gs.EcoDenuncia.controller.NeighborhoodController;
import com.gs.EcoDenuncia.model.Neighborhood;
import org.springframework.data.jpa.domain.Specification;

public class NeighborhoodSpecification {

    public static Specification<Neighborhood> withFilters(NeighborhoodController.NeighborhoodFilters filters) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filters.nome() != null && !filters.nome().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("nome")), "%" + filters.nome().toLowerCase() + "%")
                );
            }

            if (filters.cidade() != null && !filters.cidade().isEmpty()) {
                // Join para filtrar por nome da cidade
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("cidade").get("nome")), "%" + filters.cidade().toLowerCase() + "%")
                );
            }

            return predicates;
        };
    }
}
