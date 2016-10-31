package com.infinityworks.webapp.security;

import com.infinityworks.webapp.service.RemoveExpiredPasswordResetTokensService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("canvass.enableScheduling")
public class ResetPasswordTokenExpirationCheck {
    private final Logger log = LoggerFactory.getLogger(ResetPasswordTokenExpirationCheck.class);
    private final RemoveExpiredPasswordResetTokensService tokenRemovalService;

    @Autowired
    public ResetPasswordTokenExpirationCheck(RemoveExpiredPasswordResetTokensService tokenRemovalService) {
        this.tokenRemovalService = tokenRemovalService;
    }

    @Scheduled(fixedDelayString = "${canvass.passwordTokenExpiryDeletedUpdateInterval}")
    public void removeExpiredTokens() {
        long numDeleted = tokenRemovalService.removeExpiredTokens();
        if (numDeleted > 0) {
            log.info(String.format("Removed %s expired reset password tokens", numDeleted));
        }
    }
}
