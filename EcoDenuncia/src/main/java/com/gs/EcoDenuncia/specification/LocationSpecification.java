package com.gs.EcoDenuncia.specification;

import com.gs.EcoDenuncia.controller.LocationController;
import com.gs.EcoDenuncia.model.Location;
import org.springframework.data.jpa.domain.Specification;

public class LocationSpecification {

    public static Specification<Location> withFilters(LocationController.LocationFilters filters) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filters.logradouro() != null && !filters.logradouro().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("logradouro")), "%" + filters.logradouro().toLowerCase() + "%")
                );
            }

            if (filters.cep() != null && !filters.cep().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(root.get("cep"), "%" + filters.cep() + "%")
                );
            }

            if (filters.bairro() != null && !filters.bairro().isEmpty()) {
                // Para acessar nome do bairro via relacionamento
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("bairro").get("nome")), "%" + filters.bairro().toLowerCase() + "%")
                );
            }

            return predicates;
        };
    }
}
