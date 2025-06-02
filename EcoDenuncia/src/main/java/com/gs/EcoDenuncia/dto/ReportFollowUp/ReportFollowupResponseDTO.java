package com.gs.EcoDenuncia.dto.ReportFollowUp;

import com.gs.EcoDenuncia.model.ReportFollowup;
import lombok.Data;
import java.util.Date;

@Data
public class ReportFollowupResponseDTO {

    private Long id;
    private String status;
    private String descricao;
    private Date dataAtualizacao;
    private Long denunciaId;

    public ReportFollowupResponseDTO(ReportFollowup acompanhamento) {
        this.id = acompanhamento.getId();
        this.status = acompanhamento.getStatus();
        this.descricao = acompanhamento.getDescricao();
        this.dataAtualizacao = acompanhamento.getDataAtualizacao();
        this.denunciaId = acompanhamento.getDenuncia().getId();
    }
}
