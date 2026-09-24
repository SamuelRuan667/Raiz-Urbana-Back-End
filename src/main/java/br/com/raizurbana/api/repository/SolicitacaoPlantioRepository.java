package br.com.raizurbana.api.repository;

import br.com.raizurbana.api.model.SolicitacaoPlantio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoPlantioRepository
        extends JpaRepository<SolicitacaoPlantio, Long> {

    Optional<SolicitacaoPlantio> findByProtocolo(
            String protocolo
    );

    List<SolicitacaoPlantio> findByUsuarioIdOrderByIdDesc(
            Long usuarioId
    );

    List<SolicitacaoPlantio> findByBairroIgnoreCaseOrderByIdDesc(
            String bairro
    );
}