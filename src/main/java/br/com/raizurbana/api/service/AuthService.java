
package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.CadastroRequest;
import br.com.raizurbana.api.model.TipoUsuario;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.UsuarioRepository;
import br.com.raizurbana.api.util.CpfValidator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrar(CadastroRequest request) {

        TipoUsuario tipo = request.getTipo();

        if (tipo != TipoUsuario.USUARIO &&
                tipo != TipoUsuario.EMPRESA_PARCEIRA) {
            throw new IllegalArgumentException(
                    "O cadastro público permite apenas usuário ou empresa parceira."
            );
        }

        if (!request.getSenha().equals(request.getConfirmarSenha())) {
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

        String emailNormalizado =
                request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado."
            );
        }

        if (usuarioRepository.existsByCpf(cpfNormalizado)) {
            throw new IllegalArgumentException(
                    "Este CPF já está cadastrado."
            );
        }

        String cnpjNormalizado = null;
        String razaoSocial = null;
        String nomeFantasia = null;

        if (tipo == TipoUsuario.EMPRESA_PARCEIRA) {

            if (request.getCnpj() == null ||
                    request.getCnpj().isBlank()) {
                throw new IllegalArgumentException(
                        "CNPJ é obrigatório para empresas."
                );
            }

            cnpjNormalizado =
                    request.getCnpj().replaceAll("\\D", "");

            if (!validarCnpj(cnpjNormalizado)) {
                throw new IllegalArgumentException(
                        "CNPJ inválido."
                );
            }

            if (usuarioRepository.existsByCnpj(cnpjNormalizado)) {
                throw new IllegalArgumentException(
                        "Este CNPJ já está cadastrado."
                );
            }

            if (request.getRazaoSocial() == null ||
                    request.getRazaoSocial().isBlank()) {
                throw new IllegalArgumentException(
                        "Razão social é obrigatória."
                );
            }

            razaoSocial = request.getRazaoSocial().trim();

            if (request.getNomeFantasia() != null &&
                    !request.getNomeFantasia().isBlank()) {
                nomeFantasia = request.getNomeFantasia().trim();
            }
        }

        String senhaCriptografada =
                passwordEncoder.encode(request.getSenha());

        Usuario usuario = Usuario.builder()
                .nomeCompleto(request.getNomeCompleto().trim())
                .cpf(cpfNormalizado)
                .telefone(request.getTelefone().trim())
                .email(emailNormalizado)
                .cep(request.getCep().replaceAll("\\D", ""))
                .numero(request.getNumero())
                .rua(request.getRua().trim())
                .bairro(request.getBairro().trim())
                .cidade(request.getCidade().trim())
                .estado(request.getEstado().trim().toUpperCase(Locale.ROOT))
                .senhaHash(senhaCriptografada)
                .tipo(tipo)
                .cnpj(cnpjNormalizado)
                .razaoSocial(razaoSocial)
                .nomeFantasia(nomeFantasia)
                .instituicao(null)
                .emailInstitucional(null)
                .bairroAtuacao(null)
                .createdAt(LocalDateTime.now())
                .build();

        return usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String senha) {

        if (email == null || senha == null) {
            throw new IllegalArgumentException(
                    "E-mail ou senha inválidos."
            );
        }

        Usuario usuario = usuarioRepository
                .findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "E-mail ou senha inválidos."
                        )
                );

        if (!passwordEncoder.matches(
                senha,
                usuario.getSenhaHash()
        )) {
            throw new IllegalArgumentException(
                    "E-mail ou senha inválidos."
            );
        }

        return usuario;
    }

    private boolean validarCnpj(String cnpj) {

        if (cnpj == null || !cnpj.matches("\\d{14}")) {
            return false;
        }

        if (cnpj.chars().distinct().count() == 1) {
            return false;
        }

        int[] pesosPrimeiro = {
                5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2
        };

        int[] pesosSegundo = {
                6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2
        };

        int primeiroDigito =
                calcularDigitoCnpj(cnpj, pesosPrimeiro);

        int segundoDigito =
                calcularDigitoCnpj(cnpj, pesosSegundo);

        return primeiroDigito ==
                Character.getNumericValue(cnpj.charAt(12))
                &&
                segundoDigito ==
                Character.getNumericValue(cnpj.charAt(13));
    }

    private int calcularDigitoCnpj(
            String cnpj,
            int[] pesos
    ) {
        int soma = 0;

        for (int i = 0; i < pesos.length; i++) {
            soma += Character.getNumericValue(
                    cnpj.charAt(i)
            ) * pesos[i];
        }

        int resto = soma % 11;

        return resto < 2 ? 0 : 11 - resto;
    }
}
