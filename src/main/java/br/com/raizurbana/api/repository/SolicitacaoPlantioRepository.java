package br.com.raizurbana.api.repository;

import br.com.raizurbana.api.model.SolicitacaoPlantio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolicitacaoPlantioRepository extends JpaRepository<SolicitacaoPlantio, Long> {
    Optional<SolicitacaoPlantio> findByProtocolo(String protocolo);
}
