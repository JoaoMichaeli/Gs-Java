package com.gs.EcoDenuncia.dto.State;

import com.gs.EcoDenuncia.model.StateType;
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
public class StateRequestDTO {
    @NotBlank
    private String nome;

    @NotNull(message = "Campo obrigatório")
    private StateType type;
}
