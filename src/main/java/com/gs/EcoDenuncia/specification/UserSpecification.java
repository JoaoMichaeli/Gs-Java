package com.gs.EcoDenuncia.specification;

import com.gs.EcoDenuncia.controller.UserController;
import com.gs.EcoDenuncia.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> withFilters(UserController.UserFilters filters) {
        return (root, query, builder) -> {
            var predicates = builder.conjunction();

            if (filters.nome() != null && !filters.nome().isBlank()) {
                predicates.getExpressions().add(
                        builder.like(builder.lower(root.get("nome")), "%" + filters.nome().toLowerCase() + "%")
                );
            }

            if (filters.email() != null && !filters.email().isBlank()) {
                predicates.getExpressions().add(
                        builder.like(builder.lower(root.get("email")), "%" + filters.email().toLowerCase() + "%")
                );
            }

            return predicates;
        };
    }
}
