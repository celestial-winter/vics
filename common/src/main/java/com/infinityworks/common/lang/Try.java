package com.infinityworks.common.lang;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An algebraic data type representing the outcome of an operation that might fail.
 * Supports monadic operations like map and flatMap to transform the result
 * <p>
 * Usage in service:
 * <p>
 * Response response = appointmentService.getAppointment(id);
 * if (response.isSuccess()) return Try.success(appointment);
 * else return Try.failure(new ErrorObject("Failed to retrieve service with ID " + id));
 * <p>
 * Usage in resource:
 * <p>
 * Try<Appointment> outcome = service.getAppointment(id);
 * return outcome.fold(error -> Response.serverError(error).build(),
 * success -> Response.ok(success).build());
 *
 * @param <S> the type of the success object
 */
public interface Try<S> extends Serializable {

    final class Success<S> implements Try<S> {
        private final S value;

        private Success(S value) {
            this.value = value;
        }

        @Override
        public S get() {
            return value;
        }

        @Override
        public Exception getFailure() {
            throw new UnsupportedOperationException("getFailure on Success");
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }
    }

    final class Failure<S> implements Try<S> {
        private final Exception cause;

        private Failure(Exception exception) {
            cause = exception;
        }

        @Override
        public S get() {
            throw new UnsupportedOperationException("get on Failure");
        }

        @Override
        public Exception getFailure() {
            return cause;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }
    }

    /**
     * Creates a failure outcome
     *
     * @param value the failure value
     * @param <S>   the type of the success object
     * @return a service outcome containing a failure object
     * @throws NullPointerException if {@code value} is null
     */
    static <S> Try<S> failure(Exception value) {
        return new Failure<>(value);
    }

    /**
     * Creates a success outcome
     *
     * @param value the success value
     * @param <S>   the type of the success object
     * @return a service outcome containing a success object
     * @throws NullPointerException if {@code value} is null
     */
    static <S> Try<S> success(S value) {
        return new Success<>(value);
    }

    /**
     * Folds the failure or success to a response of type R
     *
     * @param onFailure the function to apply to the failure object if present
     * @param onSuccess the function to apply to the success object if present
     * @return the response generated from the applied function
     */
    default <R> R fold(Function<? super Exception, R> onFailure, Function<? super S, R> onSuccess) {
        if (isSuccess()) {
            return onSuccess.apply(get());
        } else {
            return onFailure.apply(getFailure());
        }
    }

    /**
     * Factory for creating a try from an operation that might fail
     *
     * @param operation the operation to execute
     * @param <S>       the type of the result of the operation
     * @return the operation result wrapped in a Try
     */
    static <S> Try<S> of(Supplier<S> operation) {
        try {
            return success(operation.get());
        } catch (Exception e) {
            return failure(e);
        }
    }

    /**
     * Maps over the success value if present
     *
     * @param mapper the function to apply to the success
     * @param <T>    the success type after the function application
     * @return a new {@link Try} containing the existing failure and a new
     * transformed success value. If success is empty this method only has the effect of translating the object's
     * return type.
     */
    @SuppressWarnings("unchecked")
    default <T> Try<T> map(Function<? super S, ? extends T> mapper) {
        if (isSuccess()) {
            return new Success<>(mapper.apply(get()));
        } else {
            return (Try<T>) this;
        }
    }

    /**
     * Maps over the success value if present. Like map but flattens out nested Try chains
     *
     * @param mapper the function to apply to success
     * @param <U>    The success type after function application
     * @return a new {@link Try} containing the existing failure and a new
     * transformed success value. If success is empty this method only has the effect of translating the object's
     * return type.
     */
    @SuppressWarnings("unchecked")
    default <U> Try<U> flatMap(final Function<? super S, Try<U>> mapper) {
        if (isSuccess()) {
            return mapper.apply(get());
        } else {
            return (Try<U>) this;
        }
    }

    /**
     * Alias for {@link this#flatMap(Function)}
     */
    default <U> Try<U> andThen(final Function<? super S, Try<U>> mapper) {
        return flatMap(mapper);
    }

    /**
     * Applies the given consumers to both the success and failure if their values are present
     *
     * @param failureConsumer the consumer of the failure, only applied if failure is not empty
     * @param successConsumer the consumer of the success, only applued if the success is not empty
     */
    default void accept(Consumer<Exception> failureConsumer, Consumer<S> successConsumer) {
        if (isSuccess()) {
            successConsumer.accept(get());
        } else {
            failureConsumer.accept(getFailure());
        }
    }

    default void accept(Consumer<S> successConsumer) {
        if (isSuccess()) {
            successConsumer.accept(get());
        }
    }

    /**
     * Return the success value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no success value present
     * @return the value, if present, otherwise {@code other}
     * @throws NullPointerException if other is null
     */
    default Try<S> orElse(Supplier<Try<S>> other) {
        if (isSuccess()) {
            return this;
        } else {
            return other.get();
        }
    }

    default <X extends Throwable> S orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isSuccess()) {
            return get();
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Gets the value of success
     *
     * @return the value stored in success
     * @throws UnsupportedOperationException if no such value is present
     */
    S get();

    /**
     * Gets the value of the failure
     *
     * @return the value stored in the failure
     * @throws UnsupportedOperationException if no such value is present
     */
    Exception getFailure();

    /**
     * @return true on success, false on failure
     */
    boolean isSuccess();


    /**
     * @return true on success, false on failure
     */
    boolean isFailure();
}
