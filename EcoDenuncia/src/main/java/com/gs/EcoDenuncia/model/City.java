package com.gs.EcoDenuncia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "TBL_CIDADE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cidade_seq")
    @SequenceGenerator(name = "cidade_seq", sequenceName = "SEQ_TBL_CIDADE", allocationSize = 1)
    @Column(name = "id_cidade")
    private Long id;

    @NotBlank(message = "Campo obrigatório")
    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private State estado;
}
