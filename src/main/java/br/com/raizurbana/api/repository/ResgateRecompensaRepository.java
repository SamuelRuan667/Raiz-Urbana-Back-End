package br.com.raizurbana.api.repository;

import br.com.raizurbana.api.model.ResgateRecompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResgateRecompensaRepository
        extends JpaRepository<ResgateRecompensa, Long> {

    List<ResgateRecompensa> findByUsuarioIdOrderByDataResgateDesc(
            Long usuarioId
    );
}