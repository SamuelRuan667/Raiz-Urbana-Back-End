package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.dto.RecompensaResponse;
import br.com.raizurbana.api.dto.ResgateRecompensaResponse;
import br.com.raizurbana.api.service.RecompensaService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recompensas")
public class RecompensaController {

    private final RecompensaService recompensaService;

    public RecompensaController(
            RecompensaService recompensaService
    ) {
        this.recompensaService = recompensaService;
    }

    @GetMapping
    public ResponseEntity<List<RecompensaResponse>> listarDisponiveis() {

        List<RecompensaResponse> recompensas =
                recompensaService.listarDisponiveis();

        return ResponseEntity.ok(recompensas);
    }

    @GetMapping("/{recompensaId}")
    public ResponseEntity<RecompensaResponse> buscarPorId(
            @PathVariable Long recompensaId
    ) {

        RecompensaResponse recompensa =
                recompensaService.buscarPorId(
                        recompensaId
                );

        return ResponseEntity.ok(recompensa);
    }

    @PostMapping("/{recompensaId}/resgatar")
    public ResponseEntity<ResgateRecompensaResponse> resgatar(
            @PathVariable Long recompensaId,
            Authentication authentication
    ) {

        Long usuarioId =
                (Long) authentication.getPrincipal();

        ResgateRecompensaResponse resgate =
                recompensaService.resgatar(
                        usuarioId,
                        recompensaId
                );

        return ResponseEntity.ok(resgate);
    }

    @GetMapping("/meus-resgates")
    public ResponseEntity<List<ResgateRecompensaResponse>> listarMeusResgates(
            Authentication authentication
    ) {

        Long usuarioId =
                (Long) authentication.getPrincipal();

        List<ResgateRecompensaResponse> resgates =
                recompensaService.listarResgatesUsuario(
                        usuarioId
                );

        return ResponseEntity.ok(resgates);
    }
}