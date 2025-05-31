package com.gs.EcoDenuncia.dto.State;

import com.gs.EcoDenuncia.model.StateType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateResponseDTO {
    private Long id;
    private String nome;
    private StateType type;
}
