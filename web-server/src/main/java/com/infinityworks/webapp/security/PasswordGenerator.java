package com.infinityworks.webapp.security;

import net.logstash.logback.encoder.org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class PasswordGenerator implements Supplier<String> {
    private static final int PASSWORD_LENGTH = 18;

    @Override
    public String get() {
        return RandomStringUtils.randomAlphanumeric(PASSWORD_LENGTH);
    }
}
