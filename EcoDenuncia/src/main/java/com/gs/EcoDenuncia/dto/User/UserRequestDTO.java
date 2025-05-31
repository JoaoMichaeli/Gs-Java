package com.gs.EcoDenuncia.dto.User;

import com.gs.EcoDenuncia.model.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @Size(min = 5, message = "A senha deve ter no mínimo 5 caracteres")
    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotBlank(message = "Role é obrigatória (USER ou ADMIN)")
    private RoleType role;
}
