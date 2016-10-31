package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.domain.PasswordResetToken;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.notifications.PasswordResetNotifier;
import com.infinityworks.webapp.repository.PasswordResetTokenRepository;
import com.infinityworks.webapp.rest.dto.*;
import com.infinityworks.webapp.security.PasswordResetTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * Service to handle password reset.  The reset password flow is as follows
 *
 * 1. User clicks 'Forgotten password' which triggers a POST request and invocation of this service.
 * 2. This service
 *    i)   generates and emails a token to the user
 *    ii)  stores the token in the database
 * 3. The user navigates to a password reset URL (which contains the token) by clicking the link in their email.
 *    This POSTs the token to the server and the server returns the new password in the response.
 * 4. The token gets deleted in the database (or deleted by a scheduled operation at some point if the token has expired).
 */
@Service
public class RequestPasswordResetService {

    private final UserService userService;
    private final PasswordResetNotifier passwordResetNotifier;
    private final PasswordResetTokenGenerator passwordResetTokenGenerator;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetService passwordResetService;

    @Autowired
    public RequestPasswordResetService(UserService userService,
                                       PasswordResetNotifier passwordResetNotifier,
                                       PasswordResetTokenGenerator passwordResetToken,
                                       PasswordResetTokenRepository passwordResetTokenRepository,
                                       PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetNotifier = passwordResetNotifier;
        this.passwordResetTokenGenerator = passwordResetToken;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetService = passwordResetService;
    }

    @Transactional
    public Try<RequestPasswordResetResponse> requestPasswordReset(PasswordResetRequest request) {
        return userService
                .getByUsername(request.username())
                .flatMap(user -> {
                    PasswordResetToken resetToken = passwordResetTokenGenerator.generateToken(user);
                    return passwordResetNotifier
                            .sendPasswordResetNotification(user, resetToken.getToken())
                            .map(emailResponse -> {
                                persistToken(user, resetToken);
                                return ImmutableRequestPasswordResetResponse.builder().withUsername(user.getUsername()).build();
                            });
                });
    }

    private String persistToken(User user, PasswordResetToken resetToken) {
        Optional<PasswordResetToken> existingTokenForUser = passwordResetTokenRepository.findOneByUserUsername(user.getUsername());
        if (existingTokenForUser.isPresent()) {
            passwordResetTokenRepository.delete(existingTokenForUser.get());
        }
        PasswordResetToken savedToken = passwordResetTokenRepository.save(resetToken);
        return savedToken.getToken();
    }

    @Transactional
    public Try<GeneratePasswordResetResponse> generatePasswordFromToken(GeneratePasswordFromTokenRequest request) {
        return userService
                .getByUsername(request.username())
                .flatMap(user -> {
                    Optional<PasswordResetToken> storedToken = passwordResetTokenRepository.findOneByUserUsername(user.getUsername());
                    if (storedToken.isPresent() && requestTokenMatchesStoredToken(storedToken.get().getToken(), request.token())) {
                        return Try.success(passwordResetService.resetPassword(user));
                    } else {
                        return Try.failure(new NotAuthorizedFailure("Invalid credentials"));
                    }
                });
    }

    private boolean requestTokenMatchesStoredToken(String storedToken, String requestToken) {
        return Objects.equals(storedToken, requestToken);
    }
}
