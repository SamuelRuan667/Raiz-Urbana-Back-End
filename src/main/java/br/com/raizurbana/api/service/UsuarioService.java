package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.AtualizarUsuarioRequest;
import br.com.raizurbana.api.dto.UsuarioResponse;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
    }

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
                request.getEmail().trim().toLowerCase();

        if (!usuario.getEmail().equalsIgnoreCase(novoEmail)
                && usuarioRepository.existsByEmail(novoEmail)) {

            throw new IllegalArgumentException(
                    "Este e-mail já está sendo utilizado."
            );
        }

        String cepNormalizado =
                request.getCep().replaceAll("\\D", "");

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
}