
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
                    .map(PropostaResumo::fromEntity)
                    .toList();

            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", e.getMessage())
            );
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<?> listarTodasPropostas(
            Authentication authentication
    ) {
        try {
            Long bossId = obterUsuarioId(authentication);

            List<PropostaParceria> propostas =
                    propostaService.listarTodasPropostas(bossId);

            List<PropostaResumo> resultado = propostas.stream()
                    .map(PropostaResumo::fromEntity)
                    .toList();

            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", e.getMessage())
            );
        }
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> buscarProposta(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            Long bossId = obterUsuarioId(authentication);

            PropostaParceria proposta =
                    propostaService.buscarPropostaPorId(bossId, id);

            return ResponseEntity.ok(PropostaDetalhe.fromEntity(proposta));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", e.getMessage())
            );
        }
    }

    @PatchMapping("/admin/{id}/aprovar")
    public ResponseEntity<?> aprovarProposta(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            Long bossId = obterUsuarioId(authentication);

            PropostaParceria proposta =
                    propostaService.aprovarProposta(bossId, id);

            return ResponseEntity.ok(
                    Map.of(
                            "id", proposta.getId(),
                            "status", proposta.getStatus(),
                            "mensagem", "Proposta aprovada com sucesso."
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", e.getMessage())
            );
        }
    }

    @PatchMapping("/admin/{id}/rejeitar")
    public ResponseEntity<?> rejeitarProposta(
            @PathVariable Long id,
            @RequestBody RejeicaoRequest request,
            Authentication authentication
    ) {
        try {
            Long bossId = obterUsuarioId(authentication);

            if (request == null || request.feedback() == null
                    || request.feedback().isBlank()) {
                return ResponseEntity.badRequest().body(
                        Map.of("erro", "O motivo da rejeição é obrigatório.")
                );
            }

            PropostaParceria proposta =
                    propostaService.rejeitarProposta(
                            bossId,
                            id,
                            request.feedback()
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "id", proposta.getId(),
                            "status", proposta.getStatus(),
                            "feedback", proposta.getFeedback(),
                            "mensagem", "Proposta rejeitada com sucesso."
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", e.getMessage())
            );
        }
    }

    private Long obterUsuarioId(Authentication authentication) {
        if (authentication == null
                || !(authentication.getPrincipal() instanceof Long)) {
            throw new IllegalArgumentException(
                    "Não foi possível identificar o usuário autenticado."
            );
        }

        return (Long) authentication.getPrincipal();
    }

    public record RejeicaoRequest(String feedback) {
    }

    public record PropostaResumo(
            Long id,
            String razaoSocial,
            String nomeFantasia,
            String cnpj,
            String status,
            OffsetDateTime dataEnvio,
            OffsetDateTime dataAnalise,
            String feedback
    ) {
        public static PropostaResumo fromEntity(PropostaParceria proposta) {
            return new PropostaResumo(
                    proposta.getId(),
                    proposta.getRazaoSocial(),
                    proposta.getNomeFantasia(),
                    proposta.getCnpj(),
                    proposta.getStatus(),
                    proposta.getDataEnvio(),
                    proposta.getDataAnalise(),
                    proposta.getFeedback()
            );
        }
    }

    public record PropostaDetalhe(
            Long id,
            Long usuarioId,
            String razaoSocial,
            String nomeFantasia,
            String cnpj,
            String responsavel,
            String email,
            String telefone,
            String cep,
            String rua,
            String numero,
            String bairro,
            String cidade,
            String estado,
            String proposta,
            String status,
            String feedback,
            OffsetDateTime dataEnvio,
            OffsetDateTime dataAnalise,
            Long analisadoPorId
    ) {
        public static PropostaDetalhe fromEntity(
                PropostaParceria proposta
        ) {
            return new PropostaDetalhe(
                    proposta.getId(),
                    proposta.getUsuario() == null
                            ? null
                            : proposta.getUsuario().getId(),
                    proposta.getRazaoSocial(),
                    proposta.getNomeFantasia(),
                    proposta.getCnpj(),
                    proposta.getResponsavel(),
                    proposta.getEmail(),
                    proposta.getTelefone(),
                    proposta.getCep(),
                    proposta.getRua(),
                    proposta.getNumero(),
                    proposta.getBairro(),
                    proposta.getCidade(),
                    proposta.getEstado(),
                    proposta.getProposta(),
                    proposta.getStatus(),
                    proposta.getFeedback(),
                    proposta.getDataEnvio(),
                    proposta.getDataAnalise(),
                    proposta.getAnalisadoPor() == null
                            ? null
                            : proposta.getAnalisadoPor().getId()
            );
        }
    }
}
