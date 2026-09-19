package br.com.raizurbana.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false, unique = true, length = 30)
    private String protocolo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Column(nullable = false, length = 20)
    private String cpf;

    @Column(nullable = false, length = 30)
    private String telefone;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 15)
    private String cep;

    @Column(nullable = false, length = 150)
    private String bairro;

    @Column(name = "rua_avenida", nullable = false, length = 200)
    private String ruaAvenida;

    @Column(length = 30)
    private String numero;

    @Column(name = "ponto_referencia", length = 250)
    private String pontoReferencia;

    @Column(name = "tipo_local", nullable = false, length = 100)
    private String tipoLocal;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "foto_nome", length = 255)
    private String fotoNome;

    @Column(name = "foto_tipo", length = 100)
    private String fotoTipo;

    @Column(name = "foto_dados", nullable = false)
    private byte[] fotoDados;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusSolicitacao status;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;
}