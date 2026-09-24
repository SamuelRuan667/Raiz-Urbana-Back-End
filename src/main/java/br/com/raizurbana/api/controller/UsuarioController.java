package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.dto.AtualizarUsuarioRequest;
import br.com.raizurbana.api.dto.UsuarioResponse;
import br.com.raizurbana.api.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id
    ) {

        UsuarioResponse usuario =
                usuarioService.buscarPorId(id);

        return ResponseEntity.ok(usuario);
    }

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
}