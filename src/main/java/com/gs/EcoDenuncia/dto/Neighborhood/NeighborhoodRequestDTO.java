package com.gs.EcoDenuncia.dto.Neighborhood;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeighborhoodRequestDTO {
    @NotBlank
    private String nome;

    @NotNull
    private Long idCidade;
}
