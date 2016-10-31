package com.infinityworks.webapp.service;

import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.repository.UserRepository;
import com.infinityworks.webapp.rest.dto.GeneratePasswordResetResponse;
import com.infinityworks.webapp.security.PasswordGenerator;
import com.infinityworks.webapp.security.StrongPasswordEncoder;
import org.junit.Before;
import org.junit.Test;

import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class PasswordResetServiceTest {

    private PasswordResetService underTest;
    private PasswordGenerator passwordGenerator;

    @Before
    public void setUp() throws Exception {
        UserRepository userRepository = mock(UserRepository.class);
        StrongPasswordEncoder passwordEncoder = new StrongPasswordEncoder();
        passwordGenerator = mock(PasswordGenerator.class);
        underTest = new PasswordResetService(passwordEncoder, passwordGenerator, userRepository);
    }

    @Test
    public void generatesANewRandomUserPassword() throws Exception {
        User user = user().build();
        given(passwordGenerator.get()).willReturn("somepassword");

        GeneratePasswordResetResponse password = underTest.resetPassword(user);

        assertThat(password.password(), is("somepassword"));
    }
}