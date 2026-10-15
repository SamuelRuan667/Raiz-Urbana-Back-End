
package br.com.raizurbana.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "propostas_parceria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropostaParceria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String rua;

    private String numero;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String responsavel;

    @Column(name = "cargo_responsavel")
    private String cargoResponsavel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String proposta;

    @Builder.Default
    @Column(nullable = false)
    private String status = "PENDENTE";

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "data_envio", nullable = false)
    private OffsetDateTime dataEnvio;

    @Column(name = "data_analise")
    private OffsetDateTime dataAnalise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analisado_por")
    private Usuario analisadoPor;
}