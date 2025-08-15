package com.gs.EcoDenuncia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "TBL_BAIRRO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Neighborhood {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bairro_seq")
    @SequenceGenerator(name = "bairro_seq", sequenceName = "SEQ_TBL_BAIRRO", allocationSize = 1)
    @Column(name = "id_bairro")
    private Long id;

    @NotBlank(message = "Campo obrigatório")
    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_cidade", nullable = false)
    private City cidade;
}
