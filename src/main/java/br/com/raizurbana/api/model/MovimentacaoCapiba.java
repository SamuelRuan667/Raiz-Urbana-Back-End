package br.com.raizurbana.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "movimentacoes_capiba")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoCapiba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "carteira_id",
            nullable = false
    )
    private CarteiraCapiba carteira;

    @Column(
            nullable = false,
            length = 20
    )
    private String tipo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(
            nullable = false,
            length = 255
    )
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id")
    private SolicitacaoPlantio solicitacao;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;
}