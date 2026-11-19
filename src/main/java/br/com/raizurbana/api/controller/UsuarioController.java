
package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.dto.AtualizarUsuarioRequest;
import br.com.raizurbana.api.dto.DefinirGestorRequest;
import br.com.raizurbana.api.dto.UsuarioResponse;
import br.com.raizurbana.api.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<MeuPerfilResponse> buscarMeuPerfil(
            Authentication authentication
    ) {
        Long usuarioId = obterUsuarioId(authentication);

        UsuarioResponse usuario = usuarioService.buscarPorId(usuarioId);

        MeuPerfilResponse perfil = new MeuPerfilResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getCpf(),
                usuario.getTelefone(),
                usuario.getEmail(),
                usuario.getCep(),
                usuario.getNumero(),
                usuario.getRua(),
                usuario.getBairro(),
                usuario.getCidade(),
                usuario.getEstado(),
                usuario.getTipo(),
                usuario.getCnpj(),
                usuario.getRazaoSocial(),
                usuario.getNomeFantasia()
        );

        return ResponseEntity.ok(perfil);
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarMeuPerfil(
            Authentication authentication,
            @Valid @RequestBody AtualizarUsuarioRequest request
    ) {
        Long usuarioId = obterUsuarioId(authentication);

        UsuarioResponse usuario = usuarioService.atualizar(
                usuarioId,
                request
        );

        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PatchMapping("/{id}/gestor")
    public ResponseEntity<UsuarioResponse> definirComoGestor(
            @PathVariable Long id,
            @Valid @RequestBody DefinirGestorRequest request
    ) {
        return ResponseEntity.ok(
                usuarioService.definirComoGestor(id, request)
        );
    }

    @PatchMapping("/{id}/usuario")
    public ResponseEntity<UsuarioResponse> definirComoUsuario(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                usuarioService.definirComoUsuario(id)
        );
    }

    private Long obterUsuarioId(Authentication authentication) {
        if (authentication == null ||
                !(authentication.getPrincipal() instanceof Long)) {
            throw new org.springframework.security.authentication
                    .AuthenticationCredentialsNotFoundException(
                    "Usuário não autenticado."
            );
        }

        return (Long) authentication.getPrincipal();
    }

    public record MeuPerfilResponse(
            Long id,
            String nomeCompleto,
            String cpf,
            String telefone,
            String email,
            String cep,
            String numero,
            String rua,
            String bairro,
            String cidade,
            String estado,
            br.com.raizurbana.api.model.TipoUsuario tipo,
            String cnpj,
            String razaoSocial,
            String nomeFantasia
    ) {
    }
}