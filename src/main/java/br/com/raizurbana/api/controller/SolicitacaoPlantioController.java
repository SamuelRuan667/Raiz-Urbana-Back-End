package br.com.raizurbana.api.controller;

import br.com.raizurbana.api.dto.SolicitacaoPlantioRequest;
import br.com.raizurbana.api.dto.SolicitacaoPlantioResponse;
import br.com.raizurbana.api.model.SolicitacaoPlantio;
import br.com.raizurbana.api.service.SolicitacaoPlantioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
@CrossOrigin(origins = "*") // 👈 OBRIGATÓRIO para o navegador não bloquear
public class SolicitacaoPlantioController {

    private final SolicitacaoPlantioService service;

    public SolicitacaoPlantioController(
            SolicitacaoPlantioService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SolicitacaoPlantioResponse> criarSolicitacao(
            @RequestPart("dados") @Valid SolicitacaoPlantioRequest dados,
            @RequestPart("foto") MultipartFile foto) throws IOException {

        SolicitacaoPlantioResponse response =
                service.criarSolicitacao(dados, foto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // READ - listar todas
    @GetMapping
    public ResponseEntity<List<SolicitacaoPlantioResponse>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // READ - buscar pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoPlantioResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // READ - buscar pelo protocolo
    @GetMapping("/protocolo/{protocolo}")
    public ResponseEntity<SolicitacaoPlantioResponse> consultarPorProtocolo(
            @PathVariable String protocolo) {

        SolicitacaoPlantio solicitacao =
                service.buscarPorProtocolo(protocolo);

        return ResponseEntity.ok(
                SolicitacaoPlantioResponse.fromEntity(solicitacao)
        );
    }

    // Buscar foto da solicitação
    @GetMapping("/protocolo/{protocolo}/foto")
    public ResponseEntity<byte[]> downloadFoto(
            @PathVariable String protocolo) {

        SolicitacaoPlantio solicitacao =
                service.buscarPorProtocolo(protocolo);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                solicitacao.getFotoNome() + "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                solicitacao.getFotoTipo()
                        )
                )
                .body(solicitacao.getFotoDados());
    }
}