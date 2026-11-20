package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.CarteiraCapibaResponse;
import br.com.raizurbana.api.dto.MovimentacaoCapibaResponse;
import br.com.raizurbana.api.model.CarteiraCapiba;
import br.com.raizurbana.api.model.MovimentacaoCapiba;
import br.com.raizurbana.api.model.SolicitacaoPlantio;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.CarteiraCapibaRepository;
import br.com.raizurbana.api.repository.MovimentacaoCapibaRepository;
import br.com.raizurbana.api.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarteiraCapibaService {

    private static final int RECOMPENSA_PLANTIO = 20;

    private static final String DESCRICAO_APROVACAO =
            "Recompensa por solicitação aprovada";

    private static final String DESCRICAO_CONCLUSAO =
            "Recompensa por plantio concluído";

    private final CarteiraCapibaRepository carteiraRepository;
    private final MovimentacaoCapibaRepository movimentacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarteiraCapibaService(
            CarteiraCapibaRepository carteiraRepository,
            MovimentacaoCapibaRepository movimentacaoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.carteiraRepository = carteiraRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public CarteiraCapibaResponse buscarPorUsuario(Long usuarioId) {
        CarteiraCapiba carteira = buscarCarteira(usuarioId);
        return CarteiraCapibaResponse.fromEntity(carteira);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoCapibaResponse> listarMovimentacoes(
            Long usuarioId
    ) {
        CarteiraCapiba carteira = buscarCarteira(usuarioId);

        return movimentacaoRepository
                .findByCarteiraIdOrderByCreatedAtDesc(carteira.getId())
                .stream()
                .map(MovimentacaoCapibaResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void creditarPlantioAprovado(SolicitacaoPlantio solicitacao) {
        creditarRecompensa(solicitacao, DESCRICAO_APROVACAO);
    }

    @Transactional
    public void creditarPlantioConcluido(SolicitacaoPlantio solicitacao) {
        creditarRecompensa(solicitacao, DESCRICAO_CONCLUSAO);
    }

    private void creditarRecompensa(
            SolicitacaoPlantio solicitacao,
            String descricao
    ) {
        if (solicitacao == null || solicitacao.getId() == null) {
            throw new IllegalArgumentException("Solicitação inválida.");
        }

        if (solicitacao.getUsuario() == null
                || solicitacao.getUsuario().getId() == null) {
            throw new IllegalArgumentException(
                    "A solicitação não possui usuário."
            );
        }

        boolean jaCreditado =
                movimentacaoRepository
                        .existsBySolicitacaoIdAndDescricao(
                                solicitacao.getId(),
                                descricao
                        );

        if (jaCreditado) {
            return;
        }

        Long usuarioId = solicitacao.getUsuario().getId();

        CarteiraCapiba carteira = buscarOuCriarCarteira(usuarioId);

        carteira.setSaldo(
                carteira.getSaldo() + RECOMPENSA_PLANTIO
        );

        carteiraRepository.save(carteira);

        MovimentacaoCapiba movimentacao =
                MovimentacaoCapiba.builder()
                        .carteira(carteira)
                        .tipo("ENTRADA")
                        .quantidade(RECOMPENSA_PLANTIO)
                        .descricao(descricao)
                        .solicitacao(solicitacao)
                        .build();

        movimentacaoRepository.save(movimentacao);
    }

    private CarteiraCapiba buscarCarteira(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        return carteiraRepository
                .findByUsuarioId(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Carteira Capiba não encontrada."
                        )
                );
    }

    private CarteiraCapiba buscarOuCriarCarteira(Long usuarioId) {
        return carteiraRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario =
                            usuarioRepository
                                    .findById(usuarioId)
                                    .orElseThrow(() ->
                                            new IllegalArgumentException(
                                                    "Usuário não encontrado."
                                            )
                                    );

                    CarteiraCapiba carteira =
                            CarteiraCapiba.builder()
                                    .usuario(usuario)
                                    .saldo(0)
                                    .build();

                    return carteiraRepository.save(carteira);
                });
    }
}