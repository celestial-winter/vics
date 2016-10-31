package com.infinityworks.webapp.common;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinityworks.common.lang.Try;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class RequestValidatorTest {

    private RequestValidator underTest;
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        ObjectMapper mapper = new ObjectMapper();

        underTest = new RequestValidator(validator, mapper);
    }

    @Test
    public void passesValidation() throws Exception {
        Try<User> validate = underTest.validate(new User("a", 1));

        assertThat(validate.isSuccess(), is(true));
    }

    @Test
    public void canHandleEntitySerialisationFailure() throws Exception {
        ObjectMapper mapper = mock(ObjectMapper.class);
        RequestValidator underTest = new RequestValidator(validator, mapper);
        given(mapper.writeValueAsString(any())).willThrow(new JsonGenerationException("bad json"));

        Try<User> validate = underTest.validate(new User(null, null));

        assertThat(validate.isSuccess(), is(false));
        assertThat(validate.getFailure().getMessage(), containsString("name may not be null"));
        assertThat(validate.getFailure().getMessage(), containsString("age may not be null"));
    }

    @Test
    public void failsWithMessages() throws Exception {
        Try<User> validate = underTest.validate(new User(null, null));

        assertThat(validate.isSuccess(), is(false));
        assertThat(validate.getFailure().getMessage(), containsString("name may not be null"));
        assertThat(validate.getFailure().getMessage(), containsString("age may not be null"));
    }

    private static class User {
        @NotNull
        private final String name;

        @NotNull
        private final Integer age;

        public User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }
    }
}