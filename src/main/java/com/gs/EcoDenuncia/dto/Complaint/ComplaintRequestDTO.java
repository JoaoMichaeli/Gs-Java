package com.gs.EcoDenuncia.dto.Complaint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintRequestDTO {

    @NotNull
    private Long idUsuario;

    @NotNull
    private Long idLocalizacao;

    @NotNull
    private Long idOrgao;

    @NotNull
    private LocalDateTime dataHora;

    @NotBlank
    private String descricao;
}
