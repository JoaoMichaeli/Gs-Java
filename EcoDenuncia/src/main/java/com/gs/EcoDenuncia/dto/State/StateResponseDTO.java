package com.gs.EcoDenuncia.dto.State;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateResponseDTO {
    private Long id;
    private String nome;
    private String uf;
}
