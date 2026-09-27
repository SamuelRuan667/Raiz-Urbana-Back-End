package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.dto.AtualizarUsuarioRequest;
import br.com.raizurbana.api.dto.DefinirGestorRequest;
import br.com.raizurbana.api.dto.UsuarioResponse;
import br.com.raizurbana.api.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }

    // ==========================================
    // LISTAR TODOS OS USUÁRIOS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>>
    listarTodos() {

        List<UsuarioResponse> usuarios =
                usuarioService.listarTodos();

        return ResponseEntity.ok(usuarios);
    }

    // ==========================================
    // BUSCAR USUÁRIO POR ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id
    ) {

        UsuarioResponse usuario =
                usuarioService.buscarPorId(id);

        return ResponseEntity.ok(usuario);
    }

    // ==========================================
    // ATUALIZAR USUÁRIO
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody AtualizarUsuarioRequest request
    ) {

        UsuarioResponse usuario =
                usuarioService.atualizar(
                        id,
                        request
                );

        return ResponseEntity.ok(usuario);
    }

    // ==========================================
    // TRANSFORMAR USUÁRIO EM GESTOR
    // ==========================================

    @PatchMapping("/{id}/gestor")
    public ResponseEntity<UsuarioResponse>
    definirComoGestor(
            @PathVariable Long id,
            @Valid
            @RequestBody DefinirGestorRequest request
    ) {

        UsuarioResponse usuario =
                usuarioService.definirComoGestor(
                        id,
                        request
                );

        return ResponseEntity.ok(usuario);
    }

    // ==========================================
    // TRANSFORMAR GESTOR EM USUÁRIO
    // ==========================================

    @PatchMapping("/{id}/usuario")
    public ResponseEntity<UsuarioResponse>
    definirComoUsuario(
            @PathVariable Long id
    ) {

        UsuarioResponse usuario =
                usuarioService.definirComoUsuario(id);

        return ResponseEntity.ok(usuario);
    }
}