
package br.com.raizurbana.api.repository;

import br.com.raizurbana.api.model.PropostaParceria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropostaParceriaRepository
        extends JpaRepository<PropostaParceria, Long> {

    List<PropostaParceria> findByUsuarioIdOrderByDataEnvioDesc(
            Long usuarioId
    );

    List<PropostaParceria> findAllByOrderByDataEnvioDesc();

    List<PropostaParceria> findByStatusOrderByDataEnvioDesc(
            String status
    );

    Optional<PropostaParceria> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);
}
