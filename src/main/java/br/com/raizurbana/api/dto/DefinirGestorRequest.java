package br.com.raizurbana.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefinirGestorRequest {

    @NotBlank(message = "Bairro de atuação é obrigatório")
    private String bairroAtuacao;
}