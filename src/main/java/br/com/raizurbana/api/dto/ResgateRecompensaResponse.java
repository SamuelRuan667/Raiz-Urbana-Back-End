package br.com.raizurbana.api.dto;

import br.com.raizurbana.api.model.ResgateRecompensa;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResgateRecompensaResponse {

    private Long id;
    private Long usuarioId;
    private Long recompensaId;
    private String tituloRecompensa;
    private Integer moedasGastas;
    private String codigoResgate;
    private String status;
    private OffsetDateTime dataResgate;

    public static ResgateRecompensaResponse fromEntity(
            ResgateRecompensa resgate
    ) {
        return ResgateRecompensaResponse.builder()
                .id(resgate.getId())
                .usuarioId(
                        resgate.getUsuario().getId()
                )
                .recompensaId(
                        resgate.getRecompensa().getId()
                )
                .tituloRecompensa(
                        resgate.getRecompensa().getTitulo()
                )
                .moedasGastas(
                        resgate.getMoedasGastas()
                )
                .codigoResgate(
                        resgate.getCodigoResgate()
                )
                .status(
                        resgate.getStatus()
                )
                .dataResgate(
                        resgate.getDataResgate()
                )
                .build();
    }
}