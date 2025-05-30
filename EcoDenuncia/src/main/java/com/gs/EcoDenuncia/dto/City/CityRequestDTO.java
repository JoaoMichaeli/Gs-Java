package com.gs.EcoDenuncia.dto.City;

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
public class CityRequestDTO {
    @NotBlank
    private String nome;

    @NotNull
    private Long idEstado;
}
