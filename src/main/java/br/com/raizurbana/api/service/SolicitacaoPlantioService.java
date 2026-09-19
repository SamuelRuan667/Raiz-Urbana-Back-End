package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.SolicitacaoPlantioRequest;
import br.com.raizurbana.api.dto.SolicitacaoPlantioResponse;
import br.com.raizurbana.api.model.SolicitacaoPlantio;
import br.com.raizurbana.api.model.StatusSolicitacao;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.SolicitacaoPlantioRepository;
import br.com.raizurbana.api.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
public class SolicitacaoPlantioService {

    private final SolicitacaoPlantioRepository repository;
    private final UsuarioRepository usuarioRepository;

    public SolicitacaoPlantioService(
            SolicitacaoPlantioRepository repository,
            UsuarioRepository usuarioRepository) {

        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
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

        Usuario usuario = usuarioRepository
                .findById(request.getUsuarioId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuário não encontrado com o id: "
                                        + request.getUsuarioId()
                        )
                );

        SolicitacaoPlantio solicitacao =
                SolicitacaoPlantio.builder()
                        .protocolo("TEMP-" + System.nanoTime())
                        .usuario(usuario)
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

        SolicitacaoPlantio salva =
                repository.saveAndFlush(solicitacao);

        String protocolo =
                Year.now().getValue()
                        + String.format("%06d", salva.getId());

        salva.setProtocolo(protocolo);

        salva = repository.save(salva);

        return SolicitacaoPlantioResponse.fromEntity(salva);
    }

    public SolicitacaoPlantio buscarPorProtocolo(
            String protocolo) {

        return repository.findByProtocolo(protocolo)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Solicitação não encontrada para o protocolo: "
                                        + protocolo
                        )
                );
    }

    public List<SolicitacaoPlantioResponse> listarTodas() {

        return repository.findAll()
                .stream()
                .map(SolicitacaoPlantioResponse::fromEntity)
                .toList();
    }

    public SolicitacaoPlantioResponse buscarPorId(Long id) {

        SolicitacaoPlantio solicitacao =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Solicitação não encontrada com o id: "
                                                + id
                                )
                        );

        return SolicitacaoPlantioResponse
                .fromEntity(solicitacao);
    }

    @Transactional
    public SolicitacaoPlantioResponse atualizarStatus(
            Long id,
            StatusSolicitacao novoStatus) {

        SolicitacaoPlantio solicitacao =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Solicitação não encontrada com o id: "
                                                + id
                                )
                        );

        solicitacao.setStatus(novoStatus);

        SolicitacaoPlantio atualizada =
                repository.save(solicitacao);

        return SolicitacaoPlantioResponse
                .fromEntity(atualizada);
    }

    @Transactional
    public void excluirSolicitacao(Long id) {

        SolicitacaoPlantio solicitacao =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Solicitação não encontrada com o id: "
                                                + id
                                )
                        );

        repository.delete(solicitacao);
    }
}