package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.SolicitacaoPlantioRequest;
import br.com.raizurbana.api.dto.SolicitacaoPlantioResponse;
import br.com.raizurbana.api.model.SolicitacaoPlantio;
import br.com.raizurbana.api.model.StatusSolicitacao;
import br.com.raizurbana.api.repository.SolicitacaoPlantioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
public class SolicitacaoPlantioService {

    private final SolicitacaoPlantioRepository repository;

    public SolicitacaoPlantioService(SolicitacaoPlantioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SolicitacaoPlantioResponse criarSolicitacao(
            SolicitacaoPlantioRequest request,
            MultipartFile foto) throws IOException {

        if (foto == null || foto.isEmpty()) {
            throw new IllegalArgumentException(
                    "A foto do local é obrigatória."
            );
        }

        String protocolo = "RZ-" + Year.now().getValue() + "-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 6)
                        .toUpperCase();

        SolicitacaoPlantio solicitacao = SolicitacaoPlantio.builder()
                .protocolo(protocolo)
                .nomeCompleto(request.getNomeCompleto())
                .cpf(request.getCpf())
                .telefone(request.getTelefone())
                .email(request.getEmail())
                .cep(request.getCep())
                .bairro(request.getBairro())
                .ruaAvenida(request.getRuaAvenida())
                .numero(request.getNumero())
                .pontoReferencia(request.getPontoReferencia())
                .tipoLocal(request.getTipoLocal())
                .observacoes(request.getObservacoes())
                .fotoNome(foto.getOriginalFilename())
                .fotoTipo(foto.getContentType())
                .fotoDados(foto.getBytes())
                .status(StatusSolicitacao.PENDENTE)
                .dataSolicitacao(LocalDateTime.now())
                .build();

        SolicitacaoPlantio salva = repository.save(solicitacao);

        return SolicitacaoPlantioResponse.fromEntity(salva);
    }

    public SolicitacaoPlantio buscarPorProtocolo(String protocolo) {
        return repository.findByProtocolo(protocolo)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Solicitação não encontrada para o protocolo: "
                                        + protocolo
                        )
                );
    }

    // READ - listar todas as solicitações
    public List<SolicitacaoPlantioResponse> listarTodas() {
        return repository.findAll()
                .stream()
                .map(SolicitacaoPlantioResponse::fromEntity)
                .toList();
    }

    // READ - buscar uma solicitação pelo ID
    public SolicitacaoPlantioResponse buscarPorId(Long id) {

        SolicitacaoPlantio solicitacao = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Solicitação não encontrada com o id: " + id
                        )
                );

        return SolicitacaoPlantioResponse.fromEntity(solicitacao);
    }
}