package br.com.raizurbana.api.repository;

import br.com.raizurbana.api.model.PasswordResetToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHashAndUsadoEmIsNull(
            String tokenHash
    );

    void deleteByUsuarioId(Long usuarioId);
}