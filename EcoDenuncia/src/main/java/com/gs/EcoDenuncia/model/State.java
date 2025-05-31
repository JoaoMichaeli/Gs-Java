package com.gs.EcoDenuncia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "TBL_ESTADO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_seq")
    @SequenceGenerator(name = "estado_seq", sequenceName = "SEQ_TBL_ESTADO", allocationSize = 1)
    @Column(name = "id_estado")
    private Long id;

    @NotBlank(message = "Campo obrigatório")
    private String nome;

    @NotNull(message = "Campo obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private StateType type;
}
