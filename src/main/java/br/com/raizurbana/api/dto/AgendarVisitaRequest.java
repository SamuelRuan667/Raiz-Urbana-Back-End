package br.com.raizurbana.api.dto;

import java.time.LocalDateTime;

public class AgendarVisitaRequest {

    private LocalDateTime dataVisita;

    public LocalDateTime getDataVisita() {
        return dataVisita;
    }

    public void setDataVisita(LocalDateTime dataVisita) {
        this.dataVisita = dataVisita;
    }
}