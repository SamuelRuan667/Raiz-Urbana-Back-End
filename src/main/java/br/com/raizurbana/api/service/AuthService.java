package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.CadastroRequest;
import br.com.raizurbana.api.model.TipoUsuario;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.UsuarioRepository;
import br.com.raizurbana.api.util.CpfValidator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.gestor.codigo}")
    private String codigoGestor;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrar(CadastroRequest request) {

        if (request.getTipo() == TipoUsuario.BOSS) {
            throw new IllegalArgumentException(
                    "Não é permitido criar uma conta BOSS pelo cadastro."
            );
        }

        if (!request.getSenha().equals(
                request.getConfirmarSenha())) {

            throw new IllegalArgumentException(
                    "As senhas não coincidem."
            );
        }

        String cpfNormalizado =
                request.getCpf().replaceAll("\\D", "");

        if (!CpfValidator.isValid(cpfNormalizado)) {
            throw new IllegalArgumentException(
                    "CPF inválido."
            );
        }

        if (usuarioRepository.existsByEmail(
                request.getEmail())) {

            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado."
            );
        }

        if (usuarioRepository.existsByCpf(
                cpfNormalizado)) {

            throw new IllegalArgumentException(
                    "Este CPF já está cadastrado."
            );
        }

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

            if (request.getBairroAtuacao() == null ||
                    request.getBairroAtuacao().isBlank()) {

                throw new IllegalArgumentException(
                        "Bairro de atuação é obrigatório para gestor."
                );
            }

            if (request.getCodigoGestor() == null ||
                    request.getCodigoGestor().isBlank()) {

                throw new IllegalArgumentException(
                        "Código de autorização do gestor é obrigatório."
                );
            }

            if (!codigoGestor.equals(
                    request.getCodigoGestor())) {

                throw new IllegalArgumentException(
                        "Código de autorização do gestor inválido."
                );
            }
        }

        String senhaCriptografada =
                passwordEncoder.encode(
                        request.getSenha()
                );

        Usuario usuario = Usuario.builder()
                .nomeCompleto(
                        request.getNomeCompleto()
                )
                .cpf(cpfNormalizado)
                .telefone(
                        request.getTelefone()
                )
                .email(
                        request.getEmail()
                )
                .cep(
                        request.getCep()
                                .replaceAll("\\D", "")
                )
                .numero(
                        request.getNumero()
                )
                .rua(
                        request.getRua()
                )
                .bairro(
                        request.getBairro()
                )
                .cidade(
                        request.getCidade()
                )
                .estado(
                        request.getEstado()
                )
                .senhaHash(
                        senhaCriptografada
                )
                .tipo(
                        request.getTipo()
                )
                .instituicao(
                        request.getTipo()
                                == TipoUsuario.GESTOR
                                ? request.getInstituicao()
                                : null
                )
                .emailInstitucional(
                        request.getTipo()
                                == TipoUsuario.GESTOR
                                ? request.getEmailInstitucional()
                                : null
                )
                .bairroAtuacao(
                        request.getTipo()
                                == TipoUsuario.GESTOR
                                ? request.getBairroAtuacao()
                                : null
                )
                .createdAt(
                        LocalDateTime.now()
                )
                .build();

        return usuarioRepository.save(usuario);
    }

    public Usuario login(
            String email,
            String senha
    ) {

        Usuario usuario =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "E-mail ou senha inválidos."
                                )
                        );

        boolean senhaCorreta =
                passwordEncoder.matches(
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