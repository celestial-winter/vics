package com.infinityworks.webapp.service;

import com.infinityworks.webapp.repository.PasswordResetTokenRepository;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class RemoveExpiredPasswordResetTokensServiceTest {
    private RemoveExpiredPasswordResetTokensService underTest;
    private PasswordResetTokenRepository repository;

    @Before
    public void setUp() throws Exception {
        repository = mock(PasswordResetTokenRepository.class);
        underTest = new RemoveExpiredPasswordResetTokensService(repository);
    }

    @Test
    public void removesTheExpiredPasswordToken() throws Exception {
        given(repository.deleteByExpiresLessThan(any())).willReturn(1L);

        Long numDeleted = underTest.removeExpiredTokens();

        assertThat(numDeleted, is(1L));
    }
}
