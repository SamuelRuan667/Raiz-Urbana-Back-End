package br.com.raizurbana.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitacaoPlantioRequest {

    @NotBlank(message = "O nome completo é obrigatório.")
    private String nomeCompleto;

    @NotBlank(message = "O CPF é obrigatório.")
    private String cpf;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefone;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    private String email;

    @NotBlank(message = "O CEP é obrigatório.")
    private String cep;

    @NotBlank(message = "O bairro é obrigatório.")
    private String bairro;

    @NotBlank(message = "A rua/avenida é obrigatória.")
    private String ruaAvenida;

    private String numero;

    private String pontoReferencia;

    @NotBlank(message = "O tipo do local é obrigatório.")
    private String tipoLocal;

    private String observacoes;
}
