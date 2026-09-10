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
    private String protocolo;
    private String nomeCompleto;
    private String email;
    private String ruaAvenida;
    private String bairro;
    private StatusSolicitacao status;
    private LocalDateTime dataSolicitacao;
    private String mensagem;

    public static SolicitacaoPlantioResponse fromEntity(SolicitacaoPlantio entity) {
        return SolicitacaoPlantioResponse.builder()
                .id(entity.getId())
                .protocolo(entity.getProtocolo())
                .nomeCompleto(entity.getNomeCompleto())
                .email(entity.getEmail())
                .ruaAvenida(entity.getRuaAvenida())
                .bairro(entity.getBairro())
                .status(entity.getStatus())
                .dataSolicitacao(entity.getDataSolicitacao())
                .mensagem("Solicitação de plantio enviada com sucesso!")
                .build();
    }
}
