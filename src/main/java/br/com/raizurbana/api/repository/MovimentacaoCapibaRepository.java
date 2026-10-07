package br.com.raizurbana.api.repository;

import br.com.raizurbana.api.model.MovimentacaoCapiba;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoCapibaRepository
        extends JpaRepository<MovimentacaoCapiba, Long> {

    List<MovimentacaoCapiba> findByCarteiraIdOrderByCreatedAtDesc(
            Long carteiraId
    );

    boolean existsBySolicitacaoIdAndTipo(
            Long solicitacaoId,
            String tipo
    );
}