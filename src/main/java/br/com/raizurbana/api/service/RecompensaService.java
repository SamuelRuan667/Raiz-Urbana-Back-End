package br.com.raizurbana.api.service;

import br.com.raizurbana.api.dto.RecompensaResponse;
import br.com.raizurbana.api.dto.ResgateRecompensaResponse;
import br.com.raizurbana.api.model.CarteiraCapiba;
import br.com.raizurbana.api.model.MovimentacaoCapiba;
import br.com.raizurbana.api.model.Recompensa;
import br.com.raizurbana.api.model.ResgateRecompensa;
import br.com.raizurbana.api.model.Usuario;
import br.com.raizurbana.api.repository.CarteiraCapibaRepository;
import br.com.raizurbana.api.repository.MovimentacaoCapibaRepository;
import br.com.raizurbana.api.repository.RecompensaRepository;
import br.com.raizurbana.api.repository.ResgateRecompensaRepository;
import br.com.raizurbana.api.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;
    private final ResgateRecompensaRepository resgateRepository;
    private final CarteiraCapibaRepository carteiraRepository;
    private final MovimentacaoCapibaRepository movimentacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public RecompensaService(
            RecompensaRepository recompensaRepository,
            ResgateRecompensaRepository resgateRepository,
            CarteiraCapibaRepository carteiraRepository,
            MovimentacaoCapibaRepository movimentacaoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.recompensaRepository = recompensaRepository;
        this.resgateRepository = resgateRepository;
        this.carteiraRepository = carteiraRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<RecompensaResponse> listarDisponiveis() {

        LocalDate hoje = LocalDate.now();

        return recompensaRepository
                .findByAtivoTrueOrderByIdDesc()
                .stream()
                .filter(recompensa ->
                        recompensa.getValidadeInicio() == null
                                || !hoje.isBefore(
                                recompensa.getValidadeInicio()
                        )
                )
                .filter(recompensa ->
                        recompensa.getValidadeFim() == null
                                || !hoje.isAfter(
                                recompensa.getValidadeFim()
                        )
                )
                .filter(recompensa ->
                        recompensa.getQuantidadeDisponivel() == null
                                || recompensa.getQuantidadeDisponivel() > 0
                )
                .map(RecompensaResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecompensaResponse buscarPorId(
            Long recompensaId
    ) {

        Recompensa recompensa =
                recompensaRepository
                        .findById(recompensaId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Recompensa não encontrada."
                                )
                        );

        return RecompensaResponse.fromEntity(
                recompensa
        );
    }

    @Transactional
    public ResgateRecompensaResponse resgatar(
            Long usuarioId,
            Long recompensaId
    ) {

        Usuario usuario =
                usuarioRepository
                        .findById(usuarioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Usuário não encontrado."
                                )
                        );

        Recompensa recompensa =
                recompensaRepository
                        .findByIdForUpdate(recompensaId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Recompensa não encontrada."
                                )
                        );

        validarRecompensa(recompensa);

        CarteiraCapiba carteira =
                carteiraRepository
                        .findByUsuarioIdForUpdate(usuarioId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Carteira Capiba não encontrada."
                                )
                        );

        Integer custo =
                recompensa.getCustoMoedas();

        if (carteira.getSaldo() < custo) {
            throw new IllegalArgumentException(
                    "Saldo de Moedas Capiba insuficiente."
            );
        }

        carteira.setSaldo(
                carteira.getSaldo() - custo
        );

        carteiraRepository.save(carteira);

        MovimentacaoCapiba movimentacao =
                MovimentacaoCapiba.builder()
                        .carteira(carteira)
                        .tipo("SAIDA")
                        .quantidade(custo)
                        .descricao(
                                "Resgate da recompensa: "
                                        + recompensa.getTitulo()
                        )
                        .solicitacao(null)
                        .build();

        movimentacaoRepository.save(
                movimentacao
        );

        if (recompensa.getQuantidadeDisponivel()
                != null) {

            recompensa.setQuantidadeDisponivel(
                    recompensa.getQuantidadeDisponivel() - 1
            );

            recompensaRepository.save(
                    recompensa
            );
        }

        String codigoResgate =
                gerarCodigoResgate();

        ResgateRecompensa resgate =
                ResgateRecompensa.builder()
                        .usuario(usuario)
                        .recompensa(recompensa)
                        .moedasGastas(custo)
                        .codigoResgate(codigoResgate)
                        .status("RESGATADO")
                        .build();

        ResgateRecompensa salvo =
                resgateRepository.save(resgate);

        return ResgateRecompensaResponse
                .fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public List<ResgateRecompensaResponse> listarResgatesUsuario(
            Long usuarioId
    ) {

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException(
                    "Usuário não encontrado."
            );
        }

        return resgateRepository
                .findByUsuarioIdOrderByDataResgateDesc(
                        usuarioId
                )
                .stream()
                .map(
                        ResgateRecompensaResponse::fromEntity
                )
                .toList();
    }

    private void validarRecompensa(
            Recompensa recompensa
    ) {

        if (!Boolean.TRUE.equals(
                recompensa.getAtivo()
        )) {
            throw new IllegalArgumentException(
                    "Esta recompensa não está disponível."
            );
        }

        LocalDate hoje =
                LocalDate.now();

        if (recompensa.getValidadeInicio() != null
                && hoje.isBefore(
                recompensa.getValidadeInicio()
        )) {

            throw new IllegalArgumentException(
                    "Esta recompensa ainda não está disponível."
            );
        }

        if (recompensa.getValidadeFim() != null
                && hoje.isAfter(
                recompensa.getValidadeFim()
        )) {

            throw new IllegalArgumentException(
                    "Esta recompensa está expirada."
            );
        }

        if (recompensa.getQuantidadeDisponivel()
                != null
                && recompensa.getQuantidadeDisponivel() <= 0) {

            throw new IllegalArgumentException(
                    "Esta recompensa está esgotada."
            );
        }

        if (recompensa.getCustoMoedas() == null
                || recompensa.getCustoMoedas() <= 0) {

            throw new IllegalArgumentException(
                    "Custo da recompensa inválido."
            );
        }
    }

    private String gerarCodigoResgate() {

        String codigo =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 10)
                        .toUpperCase();

        return "CAPIBA-" + codigo;
    }
}