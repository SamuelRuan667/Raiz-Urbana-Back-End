package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.dto.AgendarVisitaRequest;
import br.com.raizurbana.api.dto.ConsultaProtocoloResponse;
import br.com.raizurbana.api.dto.SolicitacaoPlantioRequest;
import br.com.raizurbana.api.dto.SolicitacaoPlantioResponse;
import br.com.raizurbana.api.model.StatusSolicitacao;
import br.com.raizurbana.api.service.SolicitacaoPlantioService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoPlantioController {

    private final SolicitacaoPlantioService service;
    private final ObjectMapper objectMapper;

    public SolicitacaoPlantioController(
            SolicitacaoPlantioService service,
            ObjectMapper objectMapper
    ) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    // =========================================================
    // CRIAR SOLICITAÇÃO
    // O usuário é identificado pelo JWT.
    // =========================================================

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<SolicitacaoPlantioResponse> criar(
            @RequestPart("dados") String dadosJson,
            @RequestPart(
                    value = "foto",
                    required = false
            ) MultipartFile foto,
            Authentication authentication
    ) throws IOException {

        Long usuarioLogadoId =
                (Long) authentication.getPrincipal();

        SolicitacaoPlantioRequest request =
                objectMapper.readValue(
                        dadosJson,
                        SolicitacaoPlantioRequest.class
                );

        return ResponseEntity.ok(
                service.criar(
                        request,
                        foto,
                        usuarioLogadoId
                )
        );
    }

    // =========================================================
    // LISTAR TODAS AS SOLICITAÇÕES
    //
    // A autorização de GESTOR/BOSS é feita no SecurityConfig.
    // =========================================================

    @GetMapping
    public ResponseEntity<List<SolicitacaoPlantioResponse>>
    listarTodas() {

        return ResponseEntity.ok(
                service.listarTodas()
        );
    }

    // =========================================================
    // BUSCAR SOLICITAÇÃO POR ID
    //
    // BOSS:
    // pode visualizar qualquer solicitação.
    //
    // GESTOR:
    // somente solicitações do próprio bairro.
    //
    // USUARIO:
    // somente suas próprias solicitações.
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoPlantioResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long usuarioLogadoId =
                (Long) authentication.getPrincipal();

        boolean boss =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_BOSS")
                        );

        boolean gestor =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_GESTOR")
                        );

        SolicitacaoPlantioResponse solicitacao =
                service.buscarPorId(id);

        // BOSS pode visualizar qualquer solicitação.
        if (boss) {

            return ResponseEntity.ok(
                    solicitacao
            );
        }

        // GESTOR só pode visualizar solicitações
        // pertencentes ao próprio bairro.
        if (gestor) {

            service.validarAcessoGestorAoBairro(
                    usuarioLogadoId,
                    id
            );

            return ResponseEntity.ok(
                    solicitacao
            );
        }

        // USUARIO comum só pode visualizar
        // uma solicitação que pertença a ele.
        if (!usuarioLogadoId.equals(
                solicitacao.getUsuarioId()
        )) {

            throw new AccessDeniedException(
                    "Você não tem permissão para acessar esta solicitação."
            );
        }

        return ResponseEntity.ok(
                solicitacao
        );
    }

    // =========================================================
    // CONSULTAR SOLICITAÇÃO POR PROTOCOLO
    // =========================================================

    @GetMapping("/protocolo/{protocolo}")
    public ResponseEntity<ConsultaProtocoloResponse>
    buscarPorProtocolo(
            @PathVariable String protocolo
    ) {

        return ResponseEntity.ok(
                service.buscarPorProtocolo(
                        protocolo
                )
        );
    }

    // =========================================================
    // LISTAR SOLICITAÇÕES DE UM USUÁRIO
    //
    // BOSS:
    // pode consultar qualquer usuário.
    //
    // GESTOR:
    // não utiliza este endpoint.
    //
    // USUARIO:
    // somente seu próprio ID.
    // =========================================================

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SolicitacaoPlantioResponse>>
    listarPorUsuario(
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {

        Long usuarioLogadoId =
                (Long) authentication.getPrincipal();

        boolean boss =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_BOSS")
                        );

        boolean gestor =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_GESTOR")
                        );

        // BOSS pode consultar solicitações
        // de qualquer usuário.
        if (boss) {

            return ResponseEntity.ok(
                    service.listarPorUsuario(
                            usuarioId
                    )
            );
        }

        // GESTOR deve utilizar seu endpoint específico.
        if (gestor) {

            throw new AccessDeniedException(
                    "Gestores devem consultar as solicitações " +
                            "através da sua área de atuação."
            );
        }

        // USUARIO comum só pode consultar
        // suas próprias solicitações.
        if (!usuarioLogadoId.equals(
                usuarioId
        )) {

            throw new AccessDeniedException(
                    "Você não tem permissão para acessar " +
                            "as solicitações deste usuário."
            );
        }

        return ResponseEntity.ok(
                service.listarPorUsuario(
                        usuarioId
                )
        );
    }

    // =========================================================
    // LISTAR SOLICITAÇÕES DO GESTOR
    //
    // BOSS:
    // pode consultar qualquer gestor.
    //
    // GESTOR:
    // somente sua própria área.
    // =========================================================

    @GetMapping("/gestor/{gestorId}")
    public ResponseEntity<List<SolicitacaoPlantioResponse>>
    listarPorGestor(
            @PathVariable Long gestorId,
            Authentication authentication
    ) {

        Long usuarioLogadoId =
                (Long) authentication.getPrincipal();

        boolean boss =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_BOSS")
                        );

        boolean gestor =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_GESTOR")
                        );

        // BOSS pode consultar qualquer gestor.
        if (boss) {

            return ResponseEntity.ok(
                    service.listarPorGestor(
                            gestorId
                    )
            );
        }

        // GESTOR só pode consultar sua própria área.
        if (gestor) {

            if (!usuarioLogadoId.equals(
                    gestorId
            )) {

                throw new AccessDeniedException(
                        "Você não tem permissão para acessar " +
                                "as solicitações de outro gestor."
                );
            }

            return ResponseEntity.ok(
                    service.listarPorGestor(
                            usuarioLogadoId
                    )
            );
        }

        throw new AccessDeniedException(
                "Você não tem permissão para acessar esta área."
        );
    }

    // =========================================================
    // ATUALIZAR STATUS
    //
    // BOSS:
    // pode alterar qualquer solicitação.
    //
    // GESTOR:
    // somente solicitações do próprio bairro.
    //
    // USUARIO:
    // não pode alterar status.
    // =========================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<SolicitacaoPlantioResponse>
    atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusSolicitacao status,
            Authentication authentication
    ) {

        Long usuarioLogadoId =
                (Long) authentication.getPrincipal();

        boolean boss =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_BOSS")
                        );

        boolean gestor =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_GESTOR")
                        );

        // BOSS pode alterar qualquer solicitação.
        if (boss) {

            return ResponseEntity.ok(
                    service.atualizarStatus(
                            id,
                            status
                    )
            );
        }

        // GESTOR só pode alterar solicitações
        // pertencentes ao próprio bairro.
        if (gestor) {

            service.validarAcessoGestorAoBairro(
                    usuarioLogadoId,
                    id
            );

            return ResponseEntity.ok(
                    service.atualizarStatus(
                            id,
                            status
                    )
            );
        }

        throw new AccessDeniedException(
                "Você não tem permissão para alterar " +
                        "o status desta solicitação."
        );
    }

    // =========================================================
    // AGENDAR VISITA
    //
    // BOSS:
    // pode agendar visita para qualquer solicitação.
    //
    // GESTOR:
    // somente solicitações do próprio bairro.
    //
    // USUARIO:
    // não pode agendar visita.
    // =========================================================

    @PatchMapping("/{id}/agendar-visita")
    public ResponseEntity<SolicitacaoPlantioResponse>
    agendarVisita(
            @PathVariable Long id,
            @RequestBody AgendarVisitaRequest request,
            Authentication authentication
    ) {

        Long usuarioLogadoId =
                (Long) authentication.getPrincipal();

        boolean boss =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_BOSS")
                        );

        boolean gestor =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_GESTOR")
                        );

        // BOSS pode agendar visita
        // para qualquer solicitação.
        if (boss) {

            return ResponseEntity.ok(
                    service.agendarVisita(
                            id,
                            request.getDataVisita()
                    )
            );
        }

        // GESTOR só pode agendar visita
        // para solicitações do próprio bairro.
        if (gestor) {

            service.validarAcessoGestorAoBairro(
                    usuarioLogadoId,
                    id
            );

            return ResponseEntity.ok(
                    service.agendarVisita(
                            id,
                            request.getDataVisita()
                    )
            );
        }

        throw new AccessDeniedException(
                "Você não tem permissão para agendar visitas."
        );
    }

    // =========================================================
    // BUSCAR FOTO
    //
    // BOSS:
    // pode acessar qualquer foto.
    //
    // GESTOR:
    // somente fotos de solicitações do próprio bairro.
    //
    // USUARIO:
    // somente foto da própria solicitação.
    // =========================================================

    @GetMapping("/{id}/foto")
    public ResponseEntity<byte[]> buscarFoto(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long usuarioLogadoId =
                (Long) authentication.getPrincipal();

        boolean boss =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_BOSS")
                        );

        boolean gestor =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_GESTOR")
                        );

        var solicitacao =
                service.buscarEntidadePorId(
                        id
                );

        // Se não for BOSS, precisamos verificar
        // as permissões do GESTOR ou USUARIO.
        if (!boss) {

            // GESTOR só pode acessar foto
            // de solicitação do próprio bairro.
            if (gestor) {

                service.validarAcessoGestorAoBairro(
                        usuarioLogadoId,
                        id
                );

            } else {

                // USUARIO comum só pode acessar
                // foto da própria solicitação.
                Long donoSolicitacaoId =
                        solicitacao.getUsuario() != null
                                ? solicitacao
                                .getUsuario()
                                .getId()
                                : null;

                if (!usuarioLogadoId.equals(
                        donoSolicitacaoId
                )) {

                    throw new AccessDeniedException(
                            "Você não tem permissão para acessar " +
                                    "a foto desta solicitação."
                    );
                }
            }
        }

        // Verifica se existe foto.
        if (solicitacao.getFoto() == null
                || solicitacao.getFoto().length == 0) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        String tipo =
                solicitacao.getFotoTipo();

        if (tipo == null
                || tipo.isBlank()) {

            tipo =
                    MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                tipo
                        )
                )
                .body(
                        solicitacao.getFoto()
                );
    }
}