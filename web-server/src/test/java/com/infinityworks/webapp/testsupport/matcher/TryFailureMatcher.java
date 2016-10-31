package com.infinityworks.webapp.testsupport.matcher;

import com.infinityworks.common.lang.Try;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class TryFailureMatcher extends TypeSafeMatcher<Try<?>> {
    private static final String DESCRIPTION = "A Try with failure item ";
    private final Matcher<?> matcher;

    public TryFailureMatcher(Matcher<?> matcher) {
        this.matcher = matcher;
    }

    public static Matcher<Try<?>> isFailure(Matcher<?> matcher) {
        return new TryFailureMatcher(matcher);
    }

    @Override
    protected boolean matchesSafely(Try<?> item) {
        return !item.isSuccess() && matcher.matches(item.getFailure());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(DESCRIPTION).appendDescriptionOf(matcher);
    }

    @Override
    protected void describeMismatchSafely(Try<?> item, Description mismatchDescription) {
        mismatchDescription.appendText(DESCRIPTION);
        if (item.isSuccess()) {
            mismatchDescription.appendText(" but was a Try success");
        } else {
            mismatchDescription.appendValue(item.getFailure());
        }
    }
}
