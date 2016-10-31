package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.domain.CurrentUser;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.UserSessionFailure;
import com.infinityworks.webapp.repository.UserRepository;
import org.apache.http.auth.BasicUserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static com.infinityworks.webapp.testsupport.matcher.TryFailureMatcher.isFailure;
import static com.infinityworks.webapp.testsupport.matcher.TrySuccessMatcher.isSuccess;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class SessionServiceTest {

    private UserRepository userRepository;
    private SessionService underTest;

    @Before
    public void setUp() throws Exception {
        userRepository = mock(UserRepository.class);
        underTest = new SessionService(userRepository);
    }

    @Test
    public void extractsUserFromSession() throws Exception {
        User amy = user().withFirstName("Amy").build();
        given(userRepository.findOne(amy.getId())).willReturn(amy);
        CurrentUser user = new CurrentUser(amy);
        Principal p = new UsernamePasswordAuthenticationToken(user, null);

        Try<User> userTry = underTest.extractUserFromPrincipal(p);

        assertThat(userTry, isSuccess(equalTo(amy)));
    }

    @Test
    public void returnsFailureIfCannotFindUser() throws Exception {
        User amy = user().withFirstName("Amy").build();
        given(userRepository.findOne(amy.getId())).willReturn(amy);
        Principal p = new BasicUserPrincipal("name");

        Try<User> userTry = underTest.extractUserFromPrincipal(p);

        assertThat(userTry, isFailure(instanceOf(UserSessionFailure.class)));
    }
}