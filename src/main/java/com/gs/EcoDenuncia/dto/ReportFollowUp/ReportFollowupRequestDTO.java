package com.gs.EcoDenuncia.dto.ReportFollowUp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportFollowupRequestDTO {

    @NotBlank
    private String status;

    @NotBlank
    private String descricao;

    @NotNull
    private Long denunciaId;
}
