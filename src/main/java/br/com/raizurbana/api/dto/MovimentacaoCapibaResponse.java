package br.com.raizurbana.api.dto;

import br.com.raizurbana.api.model.MovimentacaoCapiba;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoCapibaResponse {

    private Long id;
    private String tipo;
    private Integer quantidade;
    private String descricao;
    private Long solicitacaoId;
    private OffsetDateTime createdAt;

    public static MovimentacaoCapibaResponse fromEntity(
            MovimentacaoCapiba movimentacao
    ) {
        return MovimentacaoCapibaResponse.builder()
                .id(movimentacao.getId())
                .tipo(movimentacao.getTipo())
                .quantidade(movimentacao.getQuantidade())
                .descricao(movimentacao.getDescricao())
                .solicitacaoId(
                        movimentacao.getSolicitacao() != null
                                ? movimentacao.getSolicitacao().getId()
                                : null
                )
                .createdAt(movimentacao.getCreatedAt())
                .build();
    }
}