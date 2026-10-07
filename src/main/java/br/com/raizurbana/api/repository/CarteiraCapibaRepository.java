package br.com.raizurbana.api.repository;

import br.com.raizurbana.api.model.CarteiraCapiba;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarteiraCapibaRepository
        extends JpaRepository<CarteiraCapiba, Long> {

    Optional<CarteiraCapiba> findByUsuarioId(
            Long usuarioId
    );

    boolean existsByUsuarioId(
            Long usuarioId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT c
            FROM CarteiraCapiba c
            WHERE c.usuario.id = :usuarioId
            """)
    Optional<CarteiraCapiba> findByUsuarioIdForUpdate(
            @Param("usuarioId") Long usuarioId
    );
}