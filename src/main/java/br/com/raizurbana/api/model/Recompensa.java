package br.com.raizurbana.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "recompensas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parceiro_id")
    private Long parceiroId;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(
            name = "custo_moedas",
            nullable = false
    )
    private Integer custoMoedas;

    @Column(name = "codigo_cupom")
    private String codigoCupom;

    @Column(name = "validade_inicio")
    private LocalDate validadeInicio;

    @Column(name = "validade_fim")
    private LocalDate validadeFim;

    @Column(name = "quantidade_disponivel")
    private Integer quantidadeDisponivel;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;
}