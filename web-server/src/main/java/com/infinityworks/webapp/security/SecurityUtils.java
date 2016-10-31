package com.infinityworks.webapp.security;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.error.LoginFailure;
import com.infinityworks.webapp.rest.dto.Credentials;
import com.infinityworks.webapp.rest.dto.ImmutableCredentials;
import org.apache.commons.codec.binary.Base64;

import static com.google.common.base.Strings.isNullOrEmpty;

public class SecurityUtils {
    public static Try<Credentials> credentialsFromAuthHeader(String authHeader) {
        if (!authHeader.contains("Basic ")) {
            return loginFailure();
        } else {
            String credsPart = authHeader.replaceFirst("Basic ", "");
            String decoded = new String(Base64.decodeBase64(credsPart));
            if (!decoded.contains(":")) {
                return loginFailure();
            } else {
                String[] usernamePassword = decoded.split(":");
                if (usernamePassword.length == 2 && !isNullOrEmpty(usernamePassword[0]) && !isNullOrEmpty(usernamePassword[1])) {
                    Credentials credentials = ImmutableCredentials.builder()
                            .withUsername(usernamePassword[0])
                            .withPassword(usernamePassword[1])
                            .build();
                    return Try.success(credentials);
                } else {
                    return loginFailure();
                }
            }
        }
    }

    private static Try<Credentials> loginFailure() {
        return Try.failure(new LoginFailure("Bad credentials"));
    }
}
