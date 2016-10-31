package com.infinityworks.webapp.error;

import com.infinityworks.pafclient.error.PafApiFailure;
import com.infinityworks.pafclient.error.PafApiNotFoundFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import javax.validation.ValidationException;

/**
 * An application wide error handler.
 */
public final class RestErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(RestErrorHandler.class);
    private static final String VAGUE_ERROR_RESPONSE = "Something failed. Contact your system administrator";

    public ResponseEntity<?> mapToResponseEntity(Exception exception) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return mapToResponseEntity(exception, headers);
    }

    /**
     * Handles application errors and generates an error response
     *
     * @param exception the error to map to a response
     */
    public ResponseEntity<?> mapToResponseEntity(Exception exception, HttpHeaders headers) {

        if (exception instanceof ValidationException) {
            return new ResponseEntity<>(createError(exception), headers, HttpStatus.BAD_REQUEST);
        }

        if (exception instanceof LoginFailure) {
            return new ResponseEntity<>(createError(exception), headers, HttpStatus.UNAUTHORIZED);
        }

        if (exception instanceof BadCredentialsException) {
            ErrorEntity errorEntity = new ErrorEntity(LoginFailure.class.getSimpleName(), "Bad credentials", "");
            return new ResponseEntity<>(errorEntity, headers, HttpStatus.UNAUTHORIZED);
        }

        if (exception instanceof AccessDeniedException) {
            ErrorEntity errorEntity = new ErrorEntity(exception.getClass().getSimpleName(), "Access denied", "");
            return new ResponseEntity<>(errorEntity, headers, HttpStatus.FORBIDDEN);
        }

        if (exception instanceof NotAuthorizedFailure) {
            return new ResponseEntity<>(createError(exception), headers, HttpStatus.UNAUTHORIZED);
        }

        if (exception instanceof NotFoundFailure) {
            return new ResponseEntity<>(createError(exception), headers, HttpStatus.NOT_FOUND);
        }

        if (exception instanceof UserSessionFailure) {
            log.error("User session failure", exception);
            ErrorEntity errorEntity = new ErrorEntity(LoginFailure.class.getSimpleName(), VAGUE_ERROR_RESPONSE, "");
            return new ResponseEntity<>(errorEntity, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (exception instanceof BadRequestFailure) {
            return new ResponseEntity<>(createError(exception), headers, HttpStatus.BAD_REQUEST);
        }

        if (exception instanceof IllegalStateException) {
            return new ResponseEntity<>(createError(exception), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (exception instanceof PafApiFailure) {
            log.error(exception.getMessage(), exception);
            ErrorEntity errorEntity = new ErrorEntity(PafApiFailure.class.getSimpleName(), VAGUE_ERROR_RESPONSE, "");
            return new ResponseEntity<>(errorEntity, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (exception instanceof PafApiNotFoundFailure) {
            log.debug(exception.getMessage(), exception);
            return new ResponseEntity<>(createError(exception), headers, HttpStatus.NOT_FOUND);
        }

        if (exception instanceof ServerFailure) {
            return new ResponseEntity<>(createError(exception), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.error("Error is not mapped", exception);
        return new ResponseEntity<>(VAGUE_ERROR_RESPONSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ErrorEntity createError(Exception exception) {
        String custom;
        if (exception instanceof CanvassError) {
            custom = ((CanvassError) exception).getCustom();
        } else {
            custom = "";
        }
        return new ErrorEntity(exception.getClass().getSimpleName(), exception.getMessage(), custom);
    }
}
