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

    @Column(nullable = false, unique = true, length = 20)
    private String protocolo;

    // Dados Pessoais
    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false, length = 120)
    private String email;

    // Endereço do Plantio
    @Column(nullable = false, length = 10)
    private String cep;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Column(name = "rua_avenida", nullable = false, length = 150)
    private String ruaAvenida;

    @Column(length = 20)
    private String numero;

    @Column(name = "ponto_referencia", length = 200)
    private String pontoReferencia;

    @Column(name = "tipo_local", nullable = false, length = 50)
    private String tipoLocal;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    // Arquivo da Foto
    @Column(name = "foto_nome")
    private String fotoNome;

    @Column(name = "foto_tipo")
    private String fotoTipo;


    @Column(name = "foto_dados", nullable = false)
    private byte[] fotoDados;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusSolicitacao status;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;
}
