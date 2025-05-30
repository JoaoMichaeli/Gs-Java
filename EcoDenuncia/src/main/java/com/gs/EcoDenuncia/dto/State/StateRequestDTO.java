package com.gs.EcoDenuncia.dto.State;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateRequestDTO {
    @NotBlank
    private String nome;

    @NotBlank
    private String uf;
}
