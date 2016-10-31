package com.infinityworks.webapp.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class WardTest {
    @Test
    public void shouldImplementTheEqualsContract() throws Exception {
        EqualsVerifier.forClass(Ward.class)
                .withRedefinedSuperclass()
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
