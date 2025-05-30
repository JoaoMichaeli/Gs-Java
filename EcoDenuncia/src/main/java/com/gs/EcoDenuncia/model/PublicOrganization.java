package com.gs.EcoDenuncia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "TBL_ORGAOS_PUBLICOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PublicOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orgao_seq")
    @SequenceGenerator(name = "orgao_seq", sequenceName = "SEQ_TBL_ORGAOS_PUBLICOS", allocationSize = 1)
    @Column(name = "id_orgao")
    private Long id;

    @NotBlank(message = "Campo obrigatório")
    private String nome;

    @NotBlank(message = "Campo obrigatório")
    @Column(length = 8)
    private String areaAtuacao;
}
