package com.gs.EcoDenuncia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "TBL_ACOMPANHAMENTO_DENUNCIA")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportFollowup {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqAcompanhamento")
    @SequenceGenerator(name = "seqAcompanhamento", sequenceName = "SEQ_ACOMPANHAMENTO", allocationSize = 1)
    @Column(name = "id_acompanhamento")
    private Long id;

    @Column(nullable = false)
    private String status;

    @NotNull
    @Column(name = "data_atualizacao", nullable = false)
    private Date dataAtualizacao;

    @Column(length = 200)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "id_denuncia", nullable = false)
    private Complaint denuncia;
}
