package br.com.raizurbana.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedefinirSenhaRequest {

    @NotBlank(message = "O token é obrigatório.")
    private String token;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 8, message = "A senha deve possuir pelo menos 8 caracteres.")
    private String novaSenha;
}