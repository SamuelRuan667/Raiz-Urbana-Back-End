
package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.dto.PropostaParceriaRequest;
import br.com.raizurbana.api.model.PropostaParceria;
import br.com.raizurbana.api.service.PropostaParceriaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/propostas-parceria")
public class PropostaParceriaController {

    private final PropostaParceriaService propostaService;

    public PropostaParceriaController(
            PropostaParceriaService propostaService
    ) {
        this.propostaService = propostaService;
    }

    @PostMapping
    public ResponseEntity<?> enviarProposta(
            @Valid @RequestBody PropostaParceriaRequest request,
            Authentication authentication
    ) {
        try {
            Long usuarioId = obterUsuarioId(authentication);

            PropostaParceria proposta =
                    propostaService.enviarProposta(usuarioId, request);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "id", proposta.getId(),
                            "status", proposta.getStatus(),
                            "mensagem", "Proposta enviada com sucesso!"
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", e.getMessage())
            );
        }
    }

    @GetMapping("/minhas")
    public ResponseEntity<?> listarMinhasPropostas(
            Authentication authentication
    ) {
        try {
            Long usuarioId = obterUsuarioId(authentication);

            List<PropostaParceria> propostas =
                    propostaService.listarMinhasPropostas(usuarioId);

            List<PropostaResumo> resultado = propostas.stream()
                    .map(proposta -> new PropostaResumo(
                            proposta.getId(),
                            proposta.getRazaoSocial(),
                            proposta.getCnpj(),
                            proposta.getStatus(),
                            proposta.getDataEnvio(),
                            proposta.getFeedback()
                    ))
                    .toList();

            return ResponseEntity.ok(resultado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", e.getMessage())
            );
        }
    }

    private Long obterUsuarioId(Authentication authentication) {
        if (authentication == null ||
                !(authentication.getPrincipal() instanceof Long)) {
            throw new IllegalArgumentException(
                    "Não foi possível identificar o usuário autenticado."
            );
        }

        return (Long) authentication.getPrincipal();
    }

    public record PropostaResumo(
            Long id,
            String razaoSocial,
            String cnpj,
            String status,
            OffsetDateTime dataEnvio,
            String feedback
    ) {
    }
}
