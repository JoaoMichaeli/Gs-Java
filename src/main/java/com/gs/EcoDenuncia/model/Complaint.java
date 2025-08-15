package com.gs.EcoDenuncia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TBL_DENUNCIAS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "denuncia_seq")
    @SequenceGenerator(name = "denuncia_seq", sequenceName = "SEQ_TBL_DENUNCIAS", allocationSize = 1)
    @Column(name = "id_denuncia")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "id_localizacao", nullable = false)
    private Location localizacao;

    @NotNull
    private LocalDateTime dataHora;

    @NotBlank(message = "Campo obrigatório")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "id_orgao", nullable = false)
    private PublicOrganization orgao;

    @OneToMany(mappedBy = "denuncia", cascade = CascadeType.ALL)
    private List<ReportFollowup> acompanhamentos;
}
