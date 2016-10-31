package com.infinityworks.common.lang;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class TryTest {

    @Test
    public void shouldApplyFailureFunction() {
        Try<Integer> failure = Try.failure(new Exception("3"));
        Integer response = failure.fold(f -> 500, f -> 200);

        assertThat(response, is(equalTo(500)));
    }

    @Test
    public void shouldApplySuccessFunction() {
        Try<Integer> success = Try.success(3);
        Integer response = success.fold(f -> 500, f -> 200);

        assertThat(response, is(equalTo(200)));
    }

    @Test
    public void shouldMapOverTheTryIfSuccess() throws Exception {
        Try<String> success = Try.success("hello");

        Try<String> mapped = success.map(value -> value + " you");

        assertThat(mapped.get(), is("hello you"));
    }

    @Test
    public void shouldNotMapOverTheTryIfFailure() throws Exception {
        Try<String> failure = Try.failure(new Exception("hello"));

        Try<String> mapped = failure.map(value -> value + " you");

        assertThat(mapped.isSuccess(), is(false));
    }

    @Test
    public void shouldAcceptTheSuccessValueIfPresent() throws Exception {
        Try<String> outcome = Try.success("hello");

        outcome.accept(failure -> fail(),
                success -> assertThat(success, is("hello")));
    }

    @Test
    public void shouldAcceptTheFailureValueIfPresent() throws Exception {
        Try<Integer> outcome = Try.failure(new Exception("bad!"));

        outcome.accept(failure -> assertThat(failure.getMessage(), is("bad!")),
                success -> fail());
    }

    @Test
    public void shouldFlattenPresentNestedOptionals() throws Exception {
        Supplier<Integer> aFunctionThatReturnsAnInteger = mock(Supplier.class);
        Mockito.when(aFunctionThatReturnsAnInteger.get()).thenReturn(12345);

        Try<String> nested = Try.success("hello");

        Try<Integer> flattened = nested
                .flatMap(d -> Try.failure(new Exception("hell")))
                .map(d -> aFunctionThatReturnsAnInteger.get());

        flattened.accept(failure -> assertThat(failure.getMessage(), is("hell")),
                success -> fail());

        Mockito.verifyZeroInteractions(aFunctionThatReturnsAnInteger);
    }

    @Test
    public void shouldReturnTheFailureItemIfFlatMapIsCalledOnFailure() {
        Try<String> nested = Try.failure(new Exception("nope"));

        Try<String> other = nested.flatMap(s -> Try.success("success"));

        assertThat(other.isSuccess(), is(false));
        assertThat(other.getFailure().getMessage(), is(equalTo("nope")));
    }

    @Test
    public void shouldReturnSelfIfSuccessfulUsingOrElse() throws Exception {
        Try<String> success = Try.success("wibble");

        Try<String> other = success.orElse(() -> Try.success("Success!"));

        assertThat(other.isSuccess(), is(true));
        assertThat(other.get(), is(equalTo("wibble")));
    }

    @Test
    public void shouldInvokeASupplierIfNotSuccessfulUsingOrElse() throws Exception {
        Try<String> failure = Try.failure(new Exception("nah"));

        Try<String> other = failure.orElse(() -> Try.success("Success!"));

        assertThat(other.isSuccess(), is(true));
        assertThat(other.get(), is(equalTo("Success!")));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerIfOrElseSupplierIsNull() {
        Try<String> failure = Try.failure(new Exception("hello"));

        failure.orElse(null);
    }

    @Test
    public void getFailureReturnsFailureObject() {
        Try<Object> failure = Try.failure(new Exception("hello"));

        assertThat(failure.getFailure().getMessage(), is(equalTo("hello")));
    }

    @Test
    public void containsResultWhenSuccess() {
        Try<String> of = Try.of(() -> "hello");

        assertThat(of.get(), is("hello"));
    }

    @Test
    public void containsErrorWhenFailure() {
        Try<String> of = Try.of(() -> {
            throw new IllegalArgumentException("failed");
        });

        assertThat(of.isSuccess(), is(false));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getFailureThrowsExceptionIfIsSuccess() {
        Try.success("wibble").getFailure();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getThrowsExceptionIfIsFailure() {
        Try.failure(new Exception("hello")).get();
    }
}
