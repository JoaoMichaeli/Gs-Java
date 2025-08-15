package com.gs.EcoDenuncia.dto.User;

import com.gs.EcoDenuncia.model.RoleType;
import com.gs.EcoDenuncia.model.User;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private RoleType role;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
