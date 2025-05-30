package com.gs.EcoDenuncia.dto.City;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityResponseDTO {
    private Long id;
    private String nome;
    private String estado;
}
