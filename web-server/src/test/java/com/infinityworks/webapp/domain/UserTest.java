package com.infinityworks.webapp.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class UserTest {
    @Test
    public void implementsEqualsContract() throws Exception {
        EqualsVerifier.forClass(User.class).verify();
    }
}
