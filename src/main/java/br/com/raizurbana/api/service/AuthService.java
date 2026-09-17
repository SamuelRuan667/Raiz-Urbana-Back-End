package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.CadastroRequest;
import br.com.raizurbana.api.model.TipoUsuario;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrar(CadastroRequest request) {

        // Verifica se as senhas são iguais
        if (!request.getSenha().equals(request.getConfirmarSenha())) {
            throw new IllegalArgumentException(
                    "As senhas não coincidem."
            );
        }

        // Verifica se o e-mail já está cadastrado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado."
            );
        }

        // Verifica se o CPF já está cadastrado
        if (usuarioRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException(
                    "Este CPF já está cadastrado."
            );
        }

        // Se for gestor, exige os campos específicos
        if (request.getTipo() == TipoUsuario.GESTOR) {

            if (request.getInstituicao() == null ||
                    request.getInstituicao().isBlank()) {

                throw new IllegalArgumentException(
                        "Instituição é obrigatória para gestor."
                );
            }

            if (request.getEmailInstitucional() == null ||
                    request.getEmailInstitucional().isBlank()) {

                throw new IllegalArgumentException(
                        "E-mail institucional é obrigatório para gestor."
                );
            }
        }

        // Criptografa a senha
        String senhaCriptografada =
                passwordEncoder.encode(request.getSenha());

        Usuario usuario = Usuario.builder()
                .nomeCompleto(request.getNomeCompleto())
                .cpf(request.getCpf())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .cep(request.getCep())
                .numero(request.getNumero())
                .rua(request.getRua())
                .bairro(request.getBairro())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .senhaHash(senhaCriptografada)
                .tipo(request.getTipo())
                .instituicao(request.getInstituicao())
                .emailInstitucional(request.getEmailInstitucional())
                .createdAt(LocalDateTime.now())
                .build();

        return usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String senha) {

    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "E-mail ou senha inválidos."
                    )
            );

    boolean senhaCorreta = passwordEncoder.matches(
            senha,
            usuario.getSenhaHash()
    );

    if (!senhaCorreta) {
        throw new IllegalArgumentException(
                "E-mail ou senha inválidos."
        );
    }

    return usuario;
    }
}