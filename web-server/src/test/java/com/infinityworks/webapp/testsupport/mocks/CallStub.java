package com.infinityworks.webapp.testsupport.mocks;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

/**
 * Stubs the retrofit {@link Call} - to be used in unit tests
 */
public class CallStub<T> implements Call<T> {

    private final Response<T> response;
    private final Request request = new Request.Builder().url("http://localhost:9002").build();

    private CallStub(Response<T> response) {
        this.response = response;
    }

    public static <T> CallStub<T> success(T data) {
        return new CallStub<>(Response.success(data));
    }

    public static <T> CallStub<T> serverError() {
        return new CallStub<>(Response.error(500, ResponseBody.create(MediaType.parse("application/json"), "")));
    }

    public static <T> CallStub<T> notFound() {
        return new CallStub<>(Response.error(404, ResponseBody.create(MediaType.parse("application/json"), "")));
    }

    @Override
    public Response<T> execute() throws IOException {
        return response;
    }

    @Override
    public void enqueue(Callback<T> callback) {
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Call<T> clone() {
        return null;
    }

    @Override
    public Request request() {
        return request;
    }
}
