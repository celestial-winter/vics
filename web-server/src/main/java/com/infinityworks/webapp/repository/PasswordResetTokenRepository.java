package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findOneByUserUsername(String username);

    Long deleteByExpiresLessThan(LocalDateTime ldt);
}
