package br.com.raizurbana.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes_plantio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitacaoPlantio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String protocolo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "usuario_id",
            nullable = false
    )
    private Usuario usuario;

    @Column(
            name = "nome_completo",
            nullable = false
    )
    private String nomeCompleto;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String bairro;

    @Column(
            name = "rua_avenida",
            nullable = false
    )
    private String ruaAvenida;

    private String numero;

    @Column(name = "ponto_referencia")
    private String pontoReferencia;

    @Column(
            name = "tipo_local",
            nullable = false
    )
    private String tipoLocal;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(
            name = "foto",
            columnDefinition = "bytea"
    )
    private byte[] foto;

    @Column(name = "foto_tipo")
    private String fotoTipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao status;

    @Column(
            name = "data_solicitacao",
            nullable = false
    )
    private LocalDateTime dataSolicitacao;

    // Data e horário da visita técnica.
    @Column(name = "data_visita")
    private LocalDateTime dataVisita;
}