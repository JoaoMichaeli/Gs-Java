package com.gs.EcoDenuncia.dto.Location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationResponseDTO {
    private Long id;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private Double latitude;
    private Double longitude;
    private String bairro;
    private String cidade;
    private String estado;
}
