package com.infinityworks.webapp.security;

import com.infinityworks.webapp.service.RemoveExpiredPasswordResetTokensService;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ResetPasswordTokenExpirationCheckTest {

    private ResetPasswordTokenExpirationCheck underTest;
    private RemoveExpiredPasswordResetTokensService removeTokenService;

    @Before
    public void setUp() throws Exception {
        removeTokenService = mock(RemoveExpiredPasswordResetTokensService.class);
        underTest = new ResetPasswordTokenExpirationCheck(removeTokenService);
    }

    @Test
    public void removesTheExpiredPasswordToken() throws Exception {
        given(removeTokenService.removeExpiredTokens()).willReturn(3L);

        underTest.removeExpiredTokens();

        verify(removeTokenService, times(1)).removeExpiredTokens();
    }
}
