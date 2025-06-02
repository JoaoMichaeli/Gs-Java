package com.gs.EcoDenuncia.specification;

import com.gs.EcoDenuncia.controller.PublicOrganizationController;
import com.gs.EcoDenuncia.model.PublicOrganization;
import org.springframework.data.jpa.domain.Specification;

public class PublicOrganizationSpecification {

    public static Specification<PublicOrganization> withFilters(PublicOrganizationController.PublicOrganizationFilters filters) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filters.nome() != null && !filters.nome().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("nome")), "%" + filters.nome().toLowerCase() + "%")
                );
            }

            if (filters.areaAtuacao() != null && !filters.areaAtuacao().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("areaAtuacao")), "%" + filters.areaAtuacao().toLowerCase() + "%")
                );
            }

            return predicates;
        };
    }
}
