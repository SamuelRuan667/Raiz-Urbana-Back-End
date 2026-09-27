package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.AtualizarUsuarioRequest;
import br.com.raizurbana.api.dto.DefinirGestorRequest;
import br.com.raizurbana.api.dto.UsuarioResponse;
import br.com.raizurbana.api.model.TipoUsuario;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
    }

    // ==========================================
    // LISTAR TODOS OS USUÁRIOS
    // ==========================================

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {

        return usuarioRepository
                .findAll()
                .stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    // ==========================================
    // BUSCAR USUÁRIO POR ID
    // ==========================================

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {

        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado."
                        )
                );

        return UsuarioResponse.fromEntity(usuario);
    }

    // ==========================================
    // ATUALIZAR USUÁRIO
    // ==========================================

    @Transactional
    public UsuarioResponse atualizar(
            Long id,
            AtualizarUsuarioRequest request
    ) {

        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado."
                        )
                );

        String novoEmail =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        if (
                !usuario.getEmail()
                        .equalsIgnoreCase(novoEmail)
                        &&
                        usuarioRepository.existsByEmail(novoEmail)
        ) {

            throw new IllegalArgumentException(
                    "Este e-mail já está sendo utilizado."
            );
        }

        String cepNormalizado =
                request.getCep()
                        .replaceAll("\\D", "");

        usuario.setNomeCompleto(
                request.getNomeCompleto().trim()
        );

        usuario.setTelefone(
                request.getTelefone().trim()
        );

        usuario.setEmail(novoEmail);

        usuario.setCep(cepNormalizado);

        usuario.setNumero(
                request.getNumero() == null
                        ? null
                        : request.getNumero().trim()
        );

        usuario.setRua(
                request.getRua().trim()
        );

        usuario.setBairro(
                request.getBairro().trim()
        );

        usuario.setCidade(
                request.getCidade().trim()
        );

        usuario.setEstado(
                request.getEstado()
                        .trim()
                        .toUpperCase()
        );

        Usuario atualizado =
                usuarioRepository.save(usuario);

        return UsuarioResponse.fromEntity(atualizado);
    }

    // ==========================================
    // TRANSFORMAR USUÁRIO EM GESTOR
    // ==========================================

    @Transactional
    public UsuarioResponse definirComoGestor(
            Long usuarioId,
            DefinirGestorRequest request
    ) {

        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado."
                        )
                );

        if (usuario.getTipo() == TipoUsuario.BOSS) {
            throw new IllegalArgumentException(
                    "Uma conta BOSS não pode ser transformada em gestor."
            );
        }

        if (usuario.getTipo() == TipoUsuario.GESTOR) {
            throw new IllegalArgumentException(
                    "Este usuário já é um gestor."
            );
        }

        String bairroAtuacao =
                request.getBairroAtuacao() == null
                        ? ""
                        : request.getBairroAtuacao().trim();

        if (bairroAtuacao.isBlank()) {
            throw new IllegalArgumentException(
                    "O bairro de atuação é obrigatório."
            );
        }

        usuario.setTipo(TipoUsuario.GESTOR);
        usuario.setBairroAtuacao(bairroAtuacao);

        Usuario atualizado =
                usuarioRepository.save(usuario);

        return UsuarioResponse.fromEntity(atualizado);
    }

    // ==========================================
    // TRANSFORMAR GESTOR EM USUÁRIO
    // ==========================================

    @Transactional
    public UsuarioResponse definirComoUsuario(
            Long usuarioId
    ) {

        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado."
                        )
                );

        if (usuario.getTipo() == TipoUsuario.BOSS) {
            throw new IllegalArgumentException(
                    "Uma conta BOSS não pode ser transformada em usuário."
            );
        }

        if (usuario.getTipo() != TipoUsuario.GESTOR) {
            throw new IllegalArgumentException(
                    "Apenas uma conta GESTOR pode ser transformada em usuário."
            );
        }

        usuario.setTipo(TipoUsuario.USUARIO);

        // Ao deixar de ser gestor, não possui mais
        // um bairro específico de atuação.
        usuario.setBairroAtuacao(null);

        Usuario atualizado =
                usuarioRepository.save(usuario);

        return UsuarioResponse.fromEntity(atualizado);
    }
}