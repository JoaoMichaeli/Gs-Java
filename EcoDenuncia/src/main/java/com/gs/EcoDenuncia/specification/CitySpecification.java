package com.gs.EcoDenuncia.specification;

import com.gs.EcoDenuncia.controller.CityController;
import com.gs.EcoDenuncia.model.City;
import org.springframework.data.jpa.domain.Specification;

public class CitySpecification {

    public static Specification<City> withFilters(CityController.CityFilters filters) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (filters.nome() != null && !filters.nome().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("nome")),
                                "%" + filters.nome().toLowerCase() + "%"
                        )
                );
            }

            if (filters.estado() != null && !filters.estado().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("estado")),
                                "%" + filters.estado().toLowerCase() + "%"
                        )
                );
            }

            return predicates;
        };
    }
}
