package br.com.raizurbana.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "resgates_recompensa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResgateRecompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "usuario_id",
            nullable = false
    )
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "recompensa_id",
            nullable = false
    )
    private Recompensa recompensa;

    @Column(
            name = "moedas_gastas",
            nullable = false
    )
    private Integer moedasGastas;

    @Column(name = "codigo_resgate")
    private String codigoResgate;

    @Column(
            nullable = false,
            length = 30
    )
    private String status;

    @Column(
            name = "data_resgate",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime dataResgate;
}