package com.infinityworks.webapp.converter;

import com.infinityworks.common.lang.StringExtras;
import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.error.BadRequestFailure;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

/**
 * Converts the short form ern (without ward code) to long form (with ward code)
 * Ern short form: {polling district}-{elector number}-{elector suffix}
 * Ern long form: {ward code}-{polling district}-{elector number}-{elector suffix}
 * <p>
 * The short form is provided by volunteers and the long form is managed internally
 */
@Component
public class ErnShortFormToLongFormConverter implements BiFunction<String, String, Try<String>> {
    private static final String ERN_SHORT_FORM_REGEX = "^\\w{1,8}-\\d{1,8}-\\w{1,3}$";

    @Override
    public Try<String> apply(String wardCode, String shortErn) {
        if (!shortErn.matches(ERN_SHORT_FORM_REGEX) || StringExtras.isNullOrEmpty(wardCode)) {
            return Try.failure(new BadRequestFailure("Invalid ERN format"));
        } else {
            String longForm = wardCode + "-" + shortErn;
            return Try.success(longForm);
        }
    }
}
