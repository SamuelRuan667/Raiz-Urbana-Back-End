package br.com.raizurbana.api.dto;

import br.com.raizurbana.api.model.SolicitacaoPlantio;
import br.com.raizurbana.api.model.StatusSolicitacao;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SolicitacaoPlantioResponse {

    private Long id;
    private Long usuarioId;

    private String protocolo;

    private String nomeCompleto;
    private String cpf;
    private String telefone;
    private String email;

    private String cep;
    private String bairro;
    private String ruaAvenida;
    private String numero;
    private String pontoReferencia;

    private String tipoLocal;
    private String observacoes;

    private boolean possuiFoto;

    private StatusSolicitacao status;

    private LocalDateTime dataSolicitacao;

    public static SolicitacaoPlantioResponse fromEntity(
            SolicitacaoPlantio entity
    ) {

        return SolicitacaoPlantioResponse.builder()
                .id(entity.getId())
                .usuarioId(
                        entity.getUsuario() != null
                                ? entity.getUsuario().getId()
                                : null
                )
                .protocolo(
                        entity.getProtocolo()
                )
                .nomeCompleto(
                        entity.getNomeCompleto()
                )
                .cpf(
                        entity.getCpf()
                )
                .telefone(
                        entity.getTelefone()
                )
                .email(
                        entity.getEmail()
                )
                .cep(
                        entity.getCep()
                )
                .bairro(
                        entity.getBairro()
                )
                .ruaAvenida(
                        entity.getRuaAvenida()
                )
                .numero(
                        entity.getNumero()
                )
                .pontoReferencia(
                        entity.getPontoReferencia()
                )
                .tipoLocal(
                        entity.getTipoLocal()
                )
                .observacoes(
                        entity.getObservacoes()
                )
                .possuiFoto(
                        entity.getFoto() != null &&
                        entity.getFoto().length > 0
                )
                .status(
                        entity.getStatus()
                )
                .dataSolicitacao(
                        entity.getDataSolicitacao()
                )
                .build();
    }
}