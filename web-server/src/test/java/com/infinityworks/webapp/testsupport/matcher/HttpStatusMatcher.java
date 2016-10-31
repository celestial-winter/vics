package com.infinityworks.webapp.testsupport.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.http.ResponseEntity;

public class HttpStatusMatcher extends TypeSafeMatcher<ResponseEntity<?>> {
    private static final String DESCRIPTION = "A ResponseEntity with status ";
    private final Matcher<?> matcher;

    public HttpStatusMatcher(Matcher<?> matcher) {
        this.matcher = matcher;
    }

    public static Matcher<ResponseEntity<?>> hasStatusCode(Matcher<?> matcher) {
        return new HttpStatusMatcher(matcher);
    }

    @Override
    protected boolean matchesSafely(ResponseEntity<?> item) {
        return matcher.matches(item.getStatusCode().value());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(DESCRIPTION).appendDescriptionOf(matcher);
    }

    @Override
    protected void describeMismatchSafely(ResponseEntity<?> response, Description mismatchDescription) {
        mismatchDescription.appendText("was status " + response.getStatusCode());
    }
}
