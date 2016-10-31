package com.infinityworks.webapp.domain;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ErnTest {
    @Test
    public void shouldExtractTheErnParts() throws Exception {
        Ern ern = Ern.valueOf("E0900123-T-123-4");

        assertThat(ern.longForm(), is("E0900123-T-123-4"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsIfInvalid() throws Exception {
        Ern.valueOf("1234-11-44");
    }
}