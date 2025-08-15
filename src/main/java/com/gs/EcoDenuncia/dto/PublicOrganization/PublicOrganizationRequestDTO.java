package com.gs.EcoDenuncia.dto.PublicOrganization;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class PublicOrganizationRequestDTO {
    @NotBlank(message = "Campo obrigatório")
    String nome;

    @NotBlank(message = "Campo obrigatório")
    String areaAtuacao;
}
