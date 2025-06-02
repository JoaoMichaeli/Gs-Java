package com.gs.EcoDenuncia.dto.PublicOrganization;

import com.gs.EcoDenuncia.model.PublicOrganization;
import lombok.Data;

@Data

public class PublicOrganizationResponseDTO {
    private Long id;
    private String nome;
    private String areaAtuacao;

    public PublicOrganizationResponseDTO(PublicOrganization entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.areaAtuacao = entity.getAreaAtuacao();
    }
}
