
package br.com.raizurbana.api.dto;

import br.com.raizurbana.api.model.TipoUsuario;
import br.com.raizurbana.api.model.Usuario;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UsuarioResponse {

    private Long id;
    private String nomeCompleto;
    private String cpf;
    private String telefone;
    private String email;
    private String cep;
    private String numero;
    private String rua;
    private String bairro;
    private String cidade;
    private String estado;
    private TipoUsuario tipo;
    private String instituicao;
    private String emailInstitucional;
    private String bairroAtuacao;
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private LocalDateTime createdAt;

    public static UsuarioResponse fromEntity(
            Usuario usuario
    ) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nomeCompleto(usuario.getNomeCompleto())
                .cpf(usuario.getCpf())
                .telefone(usuario.getTelefone())
                .email(usuario.getEmail())
                .cep(usuario.getCep())
                .numero(usuario.getNumero())
                .rua(usuario.getRua())
                .bairro(usuario.getBairro())
                .cidade(usuario.getCidade())
                .estado(usuario.getEstado())
                .tipo(usuario.getTipo())
                .instituicao(usuario.getInstituicao())
                .emailInstitucional(
                        usuario.getEmailInstitucional()
                )
                .bairroAtuacao(
                        usuario.getBairroAtuacao()
                )
                .cnpj(usuario.getCnpj())
                .razaoSocial(usuario.getRazaoSocial())
                .nomeFantasia(usuario.getNomeFantasia())
                .createdAt(usuario.getCreatedAt())
                .build();
    }
}
