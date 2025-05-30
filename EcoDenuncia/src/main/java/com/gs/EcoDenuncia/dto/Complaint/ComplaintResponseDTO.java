package com.gs.EcoDenuncia.dto.Complaint;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintResponseDTO {

    private Long id;
    private String nomeUsuario;
    private String descricao;
    private LocalDateTime dataHora;
    private String nomeOrgao;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
}
