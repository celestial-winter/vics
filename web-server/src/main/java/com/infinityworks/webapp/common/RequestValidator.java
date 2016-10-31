package com.infinityworks.webapp.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinityworks.common.lang.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Validator for inbound requests
 */
public class RequestValidator {
    private static final Logger log = LoggerFactory.getLogger(RequestValidator.class);
    private static final String VALIDATION_FAILURE_TEMPLATE = "%s %s";
    private static final String LOG_FAILURE_TEMPLATE = "Received invalid request: '{}' Failed validation: {}";
    private final Validator validator;
    private final ObjectMapper mapper;

    /**
     * @param validator validator instance
     * @param mapper    used to serialise object for logging failures
     */
    public RequestValidator(Validator validator, ObjectMapper mapper) {
        this.validator = validator;
        this.mapper = mapper;
    }

    /**
     * Validate an object of type T
     *
     * @param underTest object under test
     * @param <T>       type of class under test
     * @return ServiceOutcome of failure or the input object
     */
    public <T> Try<T> validate(T underTest) {
        Set<ConstraintViolation<T>> violations = validator.validate(underTest);

        if (violations.isEmpty()) {
            return Try.success(underTest);
        } else {
            Exception failure = createFailure(violations, underTest);
            return Try.failure(failure);
        }
    }

    private <T> Exception createFailure(Set<ConstraintViolation<T>> validationResult, T underTest) {
        List<String> errors = errors(validationResult);
        String serializedObject = null;

        try {
            serializedObject = mapper.writeValueAsString(underTest);
        } catch (JsonProcessingException e) {
            log.error("Serialization failed", e);
        }

        String formattedErrors = errors.stream().collect(joining(", "));
        log.debug(LOG_FAILURE_TEMPLATE, serializedObject, formattedErrors);
        return new ValidationException(formattedErrors);
    }

    private <T> List<String> errors(Set<ConstraintViolation<T>> validationResult) {
        return validationResult.stream()
                .map(violation -> String.format(VALIDATION_FAILURE_TEMPLATE, violation.getPropertyPath(), violation.getMessage()))
                .collect(toList());
    }
}