package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.ConsultaProtocoloResponse;
import br.com.raizurbana.api.dto.SolicitacaoPlantioRequest;
import br.com.raizurbana.api.dto.SolicitacaoPlantioResponse;
import br.com.raizurbana.api.model.SolicitacaoPlantio;
import br.com.raizurbana.api.model.StatusSolicitacao;
import br.com.raizurbana.api.model.TipoUsuario;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.SolicitacaoPlantioRepository;
import br.com.raizurbana.api.repository.UsuarioRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitacaoPlantioService {

    private final SolicitacaoPlantioRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final CarteiraCapibaService carteiraCapibaService;

    public SolicitacaoPlantioService(
            SolicitacaoPlantioRepository repository,
            UsuarioRepository usuarioRepository,
            CarteiraCapibaService carteiraCapibaService
    ) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.carteiraCapibaService = carteiraCapibaService;
    }

    @Transactional
    public SolicitacaoPlantioResponse criar(
            SolicitacaoPlantioRequest request,
            MultipartFile foto,
            Long usuarioLogadoId
    ) throws IOException {

        Usuario usuario = usuarioRepository
                .findById(usuarioLogadoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário autenticado não encontrado."
                        )
                );

        SolicitacaoPlantio solicitacao =
                SolicitacaoPlantio.builder()
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
                        .status(StatusSolicitacao.PENDENTE)
                        .dataSolicitacao(LocalDateTime.now())
                        .build();

        if (foto != null && !foto.isEmpty()) {
            solicitacao.setFoto(foto.getBytes());
            solicitacao.setFotoTipo(foto.getContentType());
        }

        SolicitacaoPlantio salva =
                repository.save(solicitacao);

        salva.setProtocolo(
                String.format(
                        "%d%06d",
                        salva.getDataSolicitacao().getYear(),
                        salva.getId()
                )
        );

        salva = repository.save(salva);

        return SolicitacaoPlantioResponse
                .fromEntity(salva);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoPlantioResponse> listarTodas() {

        return repository
                .findAll()
                .stream()
                .map(SolicitacaoPlantioResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public SolicitacaoPlantioResponse buscarPorId(
            Long id
    ) {

        SolicitacaoPlantio solicitacao =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Solicitação não encontrada."
                                )
                        );

        return SolicitacaoPlantioResponse
                .fromEntity(solicitacao);
    }

    @Transactional(readOnly = true)
    public ConsultaProtocoloResponse buscarPorProtocolo(
            String protocolo
    ) {

        SolicitacaoPlantio solicitacao =
                repository.findByProtocolo(protocolo)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Solicitação não encontrada."
                                )
                        );

        return ConsultaProtocoloResponse
                .fromEntity(solicitacao);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoPlantioResponse> listarPorUsuario(
            Long usuarioId
    ) {

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException(
                    "Usuário não encontrado."
            );
        }

        return repository
                .findByUsuarioIdOrderByIdDesc(usuarioId)
                .stream()
                .map(SolicitacaoPlantioResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoPlantioResponse> listarPorGestor(
            Long gestorId
    ) {

        Usuario gestor = usuarioRepository
                .findById(gestorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Gestor não encontrado."
                        )
                );

        if (gestor.getTipo() != TipoUsuario.GESTOR
                && gestor.getTipo() != TipoUsuario.BOSS) {

            throw new IllegalArgumentException(
                    "O usuário informado não é gestor."
            );
        }

        if (gestor.getTipo() == TipoUsuario.BOSS) {
            return listarTodas();
        }

        if (gestor.getBairroAtuacao() == null
                || gestor.getBairroAtuacao().isBlank()) {

            throw new IllegalArgumentException(
                    "Este gestor não possui bairro de atuação."
            );
        }

        return repository
                .findByBairroIgnoreCaseOrderByIdDesc(
                        gestor.getBairroAtuacao()
                )
                .stream()
                .map(SolicitacaoPlantioResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public void validarAcessoGestorAoBairro(
            Long gestorId,
            Long solicitacaoId
    ) {

        Usuario gestor = usuarioRepository
                .findById(gestorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Gestor não encontrado."
                        )
                );

        if (gestor.getTipo() != TipoUsuario.GESTOR) {
            throw new AccessDeniedException(
                    "O usuário autenticado não é um gestor."
            );
        }

        if (gestor.getBairroAtuacao() == null
                || gestor.getBairroAtuacao().isBlank()) {

            throw new AccessDeniedException(
                    "Este gestor não possui bairro de atuação."
            );
        }

        SolicitacaoPlantio solicitacao =
                repository.findById(solicitacaoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Solicitação não encontrada."
                                )
                        );

        String bairroGestor =
                gestor.getBairroAtuacao().trim();

        String bairroSolicitacao =
                solicitacao.getBairro();

        if (bairroSolicitacao == null
                || !bairroGestor.equalsIgnoreCase(
                bairroSolicitacao.trim()
        )) {

            throw new AccessDeniedException(
                    "Você não tem permissão para alterar " +
                            "solicitações de outro bairro."
            );
        }
    }

    @Transactional
    public SolicitacaoPlantioResponse atualizarStatus(
            Long id,
            StatusSolicitacao status
    ) {

        SolicitacaoPlantio solicitacao =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Solicitação não encontrada."
                                )
                        );

        StatusSolicitacao statusAnterior =
                solicitacao.getStatus();

        solicitacao.setStatus(status);

        SolicitacaoPlantio atualizada =
                repository.save(solicitacao);

        if (status == StatusSolicitacao.CONCLUIDO
                && statusAnterior != StatusSolicitacao.CONCLUIDO) {

            carteiraCapibaService
                    .creditarPlantioConcluido(atualizada);
        }

        return SolicitacaoPlantioResponse
                .fromEntity(atualizada);
    }

    @Transactional
    public SolicitacaoPlantioResponse agendarVisita(
            Long id,
            LocalDateTime dataVisita
    ) {

        if (dataVisita == null) {
            throw new IllegalArgumentException(
                    "A data da visita é obrigatória."
            );
        }

        if (dataVisita.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "A data da visita deve ser futura."
            );
        }

        SolicitacaoPlantio solicitacao =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Solicitação não encontrada."
                                )
                        );

        if (solicitacao.getStatus()
                != StatusSolicitacao.APROVADO) {

            throw new IllegalArgumentException(
                    "Somente solicitações aprovadas " +
                            "podem ter visita agendada."
            );
        }

        solicitacao.setDataVisita(dataVisita);
        solicitacao.setStatus(
                StatusSolicitacao.VISITA_AGENDADA
        );

        SolicitacaoPlantio atualizada =
                repository.save(solicitacao);

        return SolicitacaoPlantioResponse
                .fromEntity(atualizada);
    }

    @Transactional(readOnly = true)
    public SolicitacaoPlantio buscarEntidadePorId(
            Long id
    ) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Solicitação não encontrada."
                        )
                );
    }
}