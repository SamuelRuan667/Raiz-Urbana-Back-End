package br.com.raizurbana.api.dto;

import br.com.raizurbana.api.model.Recompensa;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecompensaResponse {

    private Long id;
    private Long parceiroId;
    private String titulo;
    private String descricao;
    private Integer custoMoedas;
    private LocalDate validadeInicio;
    private LocalDate validadeFim;
    private Integer quantidadeDisponivel;
    private Boolean ativo;

    public static RecompensaResponse fromEntity(
            Recompensa recompensa
    ) {
        return RecompensaResponse.builder()
                .id(recompensa.getId())
                .parceiroId(recompensa.getParceiroId())
                .titulo(recompensa.getTitulo())
                .descricao(recompensa.getDescricao())
                .custoMoedas(recompensa.getCustoMoedas())
                .validadeInicio(recompensa.getValidadeInicio())
                .validadeFim(recompensa.getValidadeFim())
                .quantidadeDisponivel(
                        recompensa.getQuantidadeDisponivel()
                )
                .ativo(recompensa.getAtivo())
                .build();
    }
}