package com.gs.EcoDenuncia.dto.Neighborhood;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeighborhoodResponseDTO {
    private Long id;
    private String nome;
    private String cidade;
}
