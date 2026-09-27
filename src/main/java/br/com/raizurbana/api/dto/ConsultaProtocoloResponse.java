package br.com.raizurbana.api.dto;

import br.com.raizurbana.api.model.SolicitacaoPlantio;
import br.com.raizurbana.api.model.StatusSolicitacao;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConsultaProtocoloResponse {

    private String protocolo;
    private StatusSolicitacao status;
    private LocalDateTime dataSolicitacao;
    private String bairro;
    private String tipoLocal;

    public static ConsultaProtocoloResponse fromEntity(
            SolicitacaoPlantio entity
    ) {

        return ConsultaProtocoloResponse.builder()
                .protocolo(entity.getProtocolo())
                .status(entity.getStatus())
                .dataSolicitacao(entity.getDataSolicitacao())
                .bairro(entity.getBairro())
                .tipoLocal(entity.getTipoLocal())
                .build();
    }
}