package com.infinityworks.webapp.testsupport.matcher;

import com.infinityworks.common.lang.Try;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class TrySuccessMatcher<S> extends TypeSafeMatcher<Try<S>> {
    private static final String DESCRIPTION = "A Try with success item ";
    private final Matcher<S> matcher;

    public TrySuccessMatcher(Matcher<S> matcher) {
        this.matcher = matcher;
    }

    public static <S> Matcher<? super Try<S>> isSuccess(Matcher<S> matcher) {
        return new TrySuccessMatcher<>(matcher);
    }

    @Override
    protected boolean matchesSafely(Try<S> item) {
        return item.isSuccess() && matcher.matches(item.get());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(DESCRIPTION).appendDescriptionOf(matcher);
    }

    @Override
    protected void describeMismatchSafely(Try<S> item, Description mismatchDescription) {
        mismatchDescription.appendText(DESCRIPTION);
        if (item.isSuccess()) {
            mismatchDescription.appendValue(item.get());
        } else {
            mismatchDescription.appendText(" but was a Try failure");
        }
    }
}
