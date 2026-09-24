package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.dto.SolicitacaoPlantioRequest;
import br.com.raizurbana.api.dto.SolicitacaoPlantioResponse;
import br.com.raizurbana.api.model.StatusSolicitacao;
import br.com.raizurbana.api.service.SolicitacaoPlantioService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<SolicitacaoPlantioResponse> criar(
            @RequestPart("dados") String dadosJson,
            @RequestPart(
                    value = "foto",
                    required = false
            ) MultipartFile foto
    ) throws IOException {

        SolicitacaoPlantioRequest request =
                objectMapper.readValue(
                        dadosJson,
                        SolicitacaoPlantioRequest.class
                );

        return ResponseEntity.ok(
                service.criar(
                        request,
                        foto
                )
        );
    }

    @GetMapping
    public ResponseEntity<
            List<SolicitacaoPlantioResponse>
            > listarTodas() {

        return ResponseEntity.ok(
                service.listarTodas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoPlantioResponse>
    buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.buscarPorId(id)
        );
    }

    @GetMapping("/protocolo/{protocolo}")
    public ResponseEntity<SolicitacaoPlantioResponse>
    buscarPorProtocolo(
            @PathVariable String protocolo
    ) {

        return ResponseEntity.ok(
                service.buscarPorProtocolo(
                        protocolo
                )
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<
            List<SolicitacaoPlantioResponse>
            > listarPorUsuario(
            @PathVariable Long usuarioId
    ) {

        return ResponseEntity.ok(
                service.listarPorUsuario(
                        usuarioId
                )
        );
    }

    @GetMapping("/gestor/{gestorId}")
    public ResponseEntity<
            List<SolicitacaoPlantioResponse>
            > listarPorGestor(
            @PathVariable Long gestorId
    ) {

        return ResponseEntity.ok(
                service.listarPorGestor(
                        gestorId
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SolicitacaoPlantioResponse>
    atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusSolicitacao status
    ) {

        return ResponseEntity.ok(
                service.atualizarStatus(
                        id,
                        status
                )
        );
    }

    @GetMapping("/{id}/foto")
public ResponseEntity<byte[]> buscarFoto(
        @PathVariable Long id
) {

    var solicitacao =
            service.buscarEntidadePorId(id);

    if (solicitacao.getFoto() == null ||
            solicitacao.getFoto().length == 0) {

        return ResponseEntity.notFound().build();
    }

    String tipo =
            solicitacao.getFotoTipo();

    if (tipo == null || tipo.isBlank()) {
        tipo = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    return ResponseEntity.ok()
            .contentType(
                    MediaType.parseMediaType(tipo)
            )
            .body(
                    solicitacao.getFoto()
            );
}
}