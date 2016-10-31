package com.infinityworks.webapp.security;


import org.junit.Test;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class PasswordGeneratorTest {

    private final PasswordGenerator underTest = new PasswordGenerator();

    @Test
    public void generatesARandomPassword() throws Exception {
        String password = underTest.get();

        assertThat(password, instanceOf(String.class));
        assertThat(password.length(), greaterThanOrEqualTo(18));
    }
}