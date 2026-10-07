package br.com.raizurbana.api.repository;

import br.com.raizurbana.api.model.Recompensa;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecompensaRepository
        extends JpaRepository<Recompensa, Long> {

    List<Recompensa> findByAtivoTrueOrderByIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r
            FROM Recompensa r
            WHERE r.id = :recompensaId
            """)
    Optional<Recompensa> findByIdForUpdate(
            @Param("recompensaId") Long recompensaId
    );
}