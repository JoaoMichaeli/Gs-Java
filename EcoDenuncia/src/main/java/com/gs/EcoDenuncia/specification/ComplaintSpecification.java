package com.gs.EcoDenuncia.specification;

import com.gs.EcoDenuncia.controller.ComplaintController;
import com.gs.EcoDenuncia.model.Complaint;
import org.springframework.data.jpa.domain.Specification;

public class ComplaintSpecification {

    public static Specification<Complaint> withFilters(ComplaintController.ComplaintFilters filters) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (filters.descricao() != null && !filters.descricao().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("descricao")),
                                "%" + filters.descricao().toLowerCase() + "%"
                        )
                );
            }

            if (filters.orgaoNome() != null && !filters.orgaoNome().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("orgao").get("nome")),
                                "%" + filters.orgaoNome().toLowerCase() + "%"
                        )
                );
            }

            if (filters.localizacaoCidade() != null && !filters.localizacaoCidade().isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("localizacao").get("cidade")),
                                "%" + filters.localizacaoCidade().toLowerCase() + "%"
                        )
                );
            }

            return predicates;
        };
    }
}

