package com.infinityworks.webapp.service;

import com.infinityworks.webapp.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDateTime.now;

@Service
public class RemoveExpiredPasswordResetTokensService {
    private final PasswordResetTokenRepository tokenRepository;

    @Autowired
    public RemoveExpiredPasswordResetTokensService(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public Long removeExpiredTokens() {
        return tokenRepository.deleteByExpiresLessThan(now());
    }
}
