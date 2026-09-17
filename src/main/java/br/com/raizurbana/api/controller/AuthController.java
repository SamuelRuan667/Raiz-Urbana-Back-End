package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.dto.CadastroRequest;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import br.com.raizurbana.api.dto.LoginRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Map<String, Object>> cadastrar(
            @Valid @RequestBody CadastroRequest request) {

        Usuario usuario = authService.cadastrar(request);

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("id", usuario.getId());
        resposta.put("nomeCompleto", usuario.getNomeCompleto());
        resposta.put("email", usuario.getEmail());
        resposta.put("tipo", usuario.getTipo());
        resposta.put("mensagem", "Conta criada com sucesso!");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
        @Valid @RequestBody LoginRequest request) {

    Usuario usuario = authService.login(
            request.getEmail(),
            request.getSenha()
    );

    Map<String, Object> resposta = new HashMap<>();

    resposta.put("id", usuario.getId());
    resposta.put("nomeCompleto", usuario.getNomeCompleto());
    resposta.put("email", usuario.getEmail());
    resposta.put("tipo", usuario.getTipo());
    resposta.put("mensagem", "Login realizado com sucesso!");

    return ResponseEntity.ok(resposta);
    }
}