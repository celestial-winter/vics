package com.infinityworks.webapp.security;

import com.infinityworks.webapp.config.AppProperties;
import com.infinityworks.webapp.domain.PasswordResetToken;
import com.infinityworks.webapp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;

import static java.time.LocalDateTime.now;

@Component
public class PasswordResetTokenGenerator {
    private final SecureRandom random = new SecureRandom();
    private final int tokenExpiryMins;

    @Autowired
    public PasswordResetTokenGenerator(AppProperties appProperties) {
        tokenExpiryMins = appProperties.getPasswordResetExpirationMins();
    }

    public PasswordResetToken generateToken(User user) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(randToken());
        resetToken.setExpires(now().plusMinutes(tokenExpiryMins));
        return resetToken;
    }

    private String randToken() {
        return new BigInteger(120, random).toString(32);
    }
}
