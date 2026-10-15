
package br.com.raizurbana.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropostaParceriaRequest {

    @NotBlank(message = "Razão social é obrigatória")
    private String razaoSocial;

    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(
        regexp = "\\d{14}",
        message = "O CNPJ deve conter 14 números"
    )
    private String cnpj;

    @NotBlank(message = "Responsável é obrigatório")
    private String responsavel;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(
        regexp = "\\d{8}",
        message = "O CEP deve conter 8 números"
    )
    private String cep;

    private String numero;

    @NotBlank(message = "Rua é obrigatória")
    private String rua;

    @NotBlank(message = "Bairro é obrigatório")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(
        min = 2,
        max = 2,
        message = "O estado deve conter a sigla UF"
    )
    private String estado;

    @NotBlank(message = "Tipo de parceria é obrigatório")
    private String tipoParceria;

    @NotBlank(message = "Descrição da proposta é obrigatória")
    private String descricao;
}
