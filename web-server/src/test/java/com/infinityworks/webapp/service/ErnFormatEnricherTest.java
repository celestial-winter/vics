package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.converter.ErnShortFormToLongFormConverter;
import com.infinityworks.webapp.error.BadRequestFailure;
import org.junit.Test;

import static com.infinityworks.webapp.testsupport.matcher.TryFailureMatcher.isFailure;
import static com.infinityworks.webapp.testsupport.matcher.TrySuccessMatcher.isSuccess;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class ErnFormatEnricherTest {
    private final ErnShortFormToLongFormConverter underTest = new ErnShortFormToLongFormConverter();

    @Test
    public void formatsTheErnWithMinLengthParts() throws Exception {
        String wardCode = "E09000134";
        String ernShortForm = "PD-1-0";

        Try<String> longForm = underTest.apply(wardCode, ernShortForm);

        assertThat(longForm, isSuccess(equalTo(wardCode + "-" + ernShortForm)));
    }

    @Test
    public void formatsTheErnWithMaxLengthParts() throws Exception {
        String wardCode = "E09000134";
        String ernShortForm = "PD11-1234567-12";

        Try<String> longForm = underTest.apply(wardCode, ernShortForm);

        assertThat(longForm, isSuccess(equalTo(wardCode + "-" + ernShortForm)));
    }

    @Test
    public void failsIfElectorNumberNonNumeric() throws Exception {
        String wardCode = "E09000134";
        String ernShortForm = "PD11-abc-12";

        Try<String> longForm = underTest.apply(wardCode, ernShortForm);

        assertThat(longForm, isFailure(instanceOf(BadRequestFailure.class)));
    }

    @Test
    public void failsIfPollingDistrictTooLong() throws Exception {
        String wardCode = "E09000134";
        String ernShortForm = "P2345678910-123-0";

        Try<String> longForm = underTest.apply(wardCode, ernShortForm);

        assertThat(longForm, isFailure(instanceOf(BadRequestFailure.class)));
    }

    @Test
    public void failsIfPollingDistrictTooShort() throws Exception {
        String wardCode = "E09000134";
        String ernShortForm = "-123-0";

        Try<String> longForm = underTest.apply(wardCode, ernShortForm);

        assertThat(longForm, isFailure(instanceOf(BadRequestFailure.class)));
    }

    @Test
    public void failsIfElectorIDTooLong() throws Exception {
        String wardCode = "E09000134";
        String ernShortForm = "PD-123456789101112-0";

        Try<String> longForm = underTest.apply(wardCode, ernShortForm);

        assertThat(longForm, isFailure(instanceOf(BadRequestFailure.class)));
    }

    @Test
    public void failsIfElectorIDTooShort() throws Exception {
        String wardCode = "E09000134";
        String ernShortForm = "PD--0";

        Try<String> longForm = underTest.apply(wardCode, ernShortForm);

        assertThat(longForm, isFailure(instanceOf(BadRequestFailure.class)));
    }

    @Test
    public void failsIfElectorSuffixTooLong() throws Exception {
        String wardCode = "E09000134";
        String ernShortForm = "PD-1-12345678";

        Try<String> longForm = underTest.apply(wardCode, ernShortForm);

        assertThat(longForm, isFailure(instanceOf(BadRequestFailure.class)));
    }

    @Test
    public void failsIfElectorSuffixTooShort() throws Exception {
        String wardCode = "E09000134";
        String ernShortForm = "PD-1-";

        Try<String> longForm = underTest.apply(wardCode, ernShortForm);

        assertThat(longForm, isFailure(instanceOf(BadRequestFailure.class)));
    }

    @Test
    public void failsIfWardCodeEmpty() throws Exception {
        String wardCode = "";
        String ernShortForm = "PD1-122-33";

        Try<String> longForm = underTest.apply(wardCode, ernShortForm);

        assertThat(longForm, isFailure(instanceOf(BadRequestFailure.class)));
    }
}
