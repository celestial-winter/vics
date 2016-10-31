package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.clients.email.ImmutableEmailResponse;
import com.infinityworks.webapp.config.AppProperties;
import com.infinityworks.webapp.domain.PasswordResetToken;
import com.infinityworks.webapp.error.NotFoundFailure;
import com.infinityworks.webapp.notifications.PasswordResetNotifier;
import com.infinityworks.webapp.repository.PasswordResetTokenRepository;
import com.infinityworks.webapp.rest.dto.ImmutableGeneratePasswordResetResponse;
import com.infinityworks.webapp.rest.dto.ImmutablePasswordResetRequest;
import com.infinityworks.webapp.rest.dto.PasswordResetRequest;
import com.infinityworks.webapp.rest.dto.RequestPasswordResetResponse;
import com.infinityworks.webapp.security.PasswordResetTokenGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.infinityworks.webapp.testsupport.Fixtures.token;
import static com.infinityworks.webapp.testsupport.matcher.TryFailureMatcher.isFailure;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class RequestPasswordResetServiceTest {

    private RequestPasswordResetService underTest;
    private UserService userService;
    private PasswordResetNotifier passwordResetNotifier;
    private PasswordResetTokenGenerator passwordResetTokenGenerator;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private PasswordResetService passwordResetService;

    @Before
    public void setUp() throws Exception {
        AppProperties props = mock(AppProperties.class);
        when(props.getPasswordResetExpirationMins()).thenReturn(20);
        passwordResetNotifier = mock(PasswordResetNotifier.class);
        passwordResetTokenRepository = mock(PasswordResetTokenRepository.class);
        passwordResetTokenGenerator = mock(PasswordResetTokenGenerator.class);
        passwordResetService = mock(PasswordResetService.class);
        userService = mock(UserService.class);
        underTest = new RequestPasswordResetService(
                userService, passwordResetNotifier, passwordResetTokenGenerator, passwordResetTokenRepository, passwordResetService);
    }

    @Test
    public void requestsPasswordReset() throws Exception {
        PasswordResetToken token = token();

        given(passwordResetTokenRepository.findOneByUserUsername(token.getUser().getUsername())).willReturn(Optional.empty());
        given(passwordResetTokenRepository.save((PasswordResetToken) notNull())).willReturn(token);
        given(passwordResetTokenGenerator.generateToken(token.getUser())).willReturn(token);
        given(passwordResetNotifier.sendPasswordResetNotification(token.getUser(), token.getToken())).willReturn(Try.success(ImmutableEmailResponse.builder().withMessage("message").build()));
        given(passwordResetService.resetPassword(token.getUser())).willReturn(ImmutableGeneratePasswordResetResponse.builder().withPassword(token.getToken()).build());
        given(userService.getByUsername(token.getUser().getUsername())).willReturn(Try.success(token.getUser()));
        PasswordResetRequest request = ImmutablePasswordResetRequest.builder().withUsername(token.getUser().getUsername()).build();

        Try<RequestPasswordResetResponse> uname = underTest.requestPasswordReset(request);

        assertThat(uname.get().username(), equalTo(token.getUser().getUsername()));
        verify(passwordResetNotifier, times(1)).sendPasswordResetNotification(token.getUser(), token.getToken());
    }

    @Test
    public void returnsNotFoundErrorIfUserDoesNotExist() throws Exception {
        String username = "idontexist@nothing.me";
        given(userService.getByUsername(username)).willReturn(Try.failure(new NotFoundFailure("No user with name")));
        PasswordResetRequest request = ImmutablePasswordResetRequest.builder().withUsername(username).build();

        Try<RequestPasswordResetResponse> uname = underTest.requestPasswordReset(request);

        assertThat(uname, isFailure(instanceOf(NotFoundFailure.class)));
        verifyZeroInteractions(passwordResetNotifier);
    }
}
