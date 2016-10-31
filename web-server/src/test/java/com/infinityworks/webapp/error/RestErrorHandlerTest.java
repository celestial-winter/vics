package com.infinityworks.webapp.error;

import com.infinityworks.pafclient.error.PafApiFailure;
import com.infinityworks.pafclient.error.PafApiNotFoundFailure;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import javax.validation.ValidationException;

import static com.infinityworks.webapp.testsupport.matcher.HttpStatusMatcher.hasStatusCode;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class RestErrorHandlerTest {
    private RestErrorHandler underTest = new RestErrorHandler();

    @Test
    public void mapsValidationExceptionToBadRequest() throws Exception {
        ResponseEntity<?> responseEntity = underTest.mapToResponseEntity(new ValidationException("error"));

        assertThat(responseEntity, hasStatusCode(equalTo(400)));
    }

    @Test
    public void mapsLoginFailureToUnauthorized() throws Exception {
        ResponseEntity<?> responseEntity = underTest.mapToResponseEntity(new LoginFailure("error"));

        assertThat(responseEntity, hasStatusCode(equalTo(401)));
    }

    @Test
    public void mapsBadCredentialsToUnauthorized() throws Exception {
        ResponseEntity<?> responseEntity = underTest.mapToResponseEntity(new BadCredentialsException("error"));

        assertThat(responseEntity, hasStatusCode(equalTo(401)));
    }

    @Test
    public void mapsAccessDeniedToForbidden() throws Exception {
        ResponseEntity<?> responseEntity = underTest.mapToResponseEntity(new AccessDeniedException("error"));

        assertThat(responseEntity, hasStatusCode(equalTo(403)));
    }

    @Test
    public void mapsPafApiFailureToServerError() throws Exception {
        ResponseEntity<?> responseEntity = underTest.mapToResponseEntity(new PafApiFailure("error"));

        assertThat(responseEntity, hasStatusCode(equalTo(500)));
    }

    @Test
    public void unmappedFailureToServerError() throws Exception {
        ResponseEntity<?> responseEntity = underTest.mapToResponseEntity(new NonExistantFailure());

        assertThat(responseEntity, hasStatusCode(equalTo(500)));
    }

    @Test
    public void mapsPafApiNotFoundFailure() throws Exception {
        ResponseEntity<?> responseEntity = underTest.mapToResponseEntity(new PafApiNotFoundFailure("failure"));

        assertThat(responseEntity, hasStatusCode(equalTo(404)));
    }

    private static class NonExistantFailure extends Exception {}
}