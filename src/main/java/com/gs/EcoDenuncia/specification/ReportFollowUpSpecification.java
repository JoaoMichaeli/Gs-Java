package com.gs.EcoDenuncia.specification;

import com.gs.EcoDenuncia.controller.ReportFollowupController;
import com.gs.EcoDenuncia.model.ReportFollowup;
import org.springframework.data.jpa.domain.Specification;

public class ReportFollowUpSpecification {

    public static Specification<ReportFollowup> withFilters(ReportFollowupController.ReportFollowupFilters filters) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filters.status() != null && !filters.status().isEmpty()) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("status")), "%" + filters.status().toLowerCase() + "%")
                );
            }

            return predicates;
        };
    }
}
