package br.com.raizurbana.api.dto;

import br.com.raizurbana.api.model.CarteiraCapiba;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarteiraCapibaResponse {

    private Long id;
    private Long usuarioId;
    private Integer saldo;

    public static CarteiraCapibaResponse fromEntity(
            CarteiraCapiba carteira
    ) {
        return CarteiraCapibaResponse.builder()
                .id(carteira.getId())
                .usuarioId(carteira.getUsuario().getId())
                .saldo(carteira.getSaldo())
                .build();
    }
}