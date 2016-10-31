package com.infinityworks.webapp.security;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.rest.dto.Credentials;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SecurityUtilsTest {
    @Test
    public void returnsFailureIfAuthHeaderIsJunk() throws Exception {
        String header = "Basic 8wgq8wgweg0qweg=";

        Try<Credentials> credentialsTry = SecurityUtils.credentialsFromAuthHeader(header);

        assertThat(credentialsTry.isSuccess(), is(false));
    }

    @Test
    public void returnsFailureWhenPasswordIsNull() throws Exception {
        String header = "Basic YWRtaW46";

        Try<Credentials> credentialsTry = SecurityUtils.credentialsFromAuthHeader(header);

        assertThat(credentialsTry.isSuccess(), is(false));
    }

    @Test
    public void returnsFailureWhenUsernameIsNull() throws Exception {
        String header = "Basic OmFzZGFkc2FzZA==";

        Try<Credentials> credentialsTry = SecurityUtils.credentialsFromAuthHeader(header);

        assertThat(credentialsTry.isSuccess(), is(false));
    }
}