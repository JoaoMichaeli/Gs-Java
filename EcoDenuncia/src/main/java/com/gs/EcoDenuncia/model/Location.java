package com.gs.EcoDenuncia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "TBL_LOCALIZACAO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "localizacao_seq")
    @SequenceGenerator(name = "localizacao_seq", sequenceName = "SEQ_TBL_LOCALIZACAO", allocationSize = 1)
    @Column(name = "id_localizacao")
    private Long id;

    @NotBlank(message = "Campo obrigatório")
    private String logradouro;

    @NotBlank(message = "Campo obrigatório")
    private String numero;

    private String complemento;

    @NotBlank(message = "Campo obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    private String cep;

    @ManyToOne
    @JoinColumn(name = "id_bairro", nullable = false)
    private Neighborhood bairro;
}
