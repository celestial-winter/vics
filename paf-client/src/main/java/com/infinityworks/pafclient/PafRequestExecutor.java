package com.infinityworks.pafclient;

import com.infinityworks.common.lang.Try;
import com.infinityworks.common.logging.LambdaLogger;
import com.infinityworks.pafclient.error.PafApiFailure;
import com.infinityworks.pafclient.error.PafApiNotFoundFailure;
import com.infinityworks.pafclient.error.PafApiValidationFailure;
import retrofit2.Call;
import retrofit2.Response;

import java.util.UUID;

public class PafRequestExecutor {
    private static final LambdaLogger log = LambdaLogger.getLogger(PafRequestExecutor.class);
    private static final String FORMAT = "Error calling PAF API %s. Response contained status code=%d, status text=%s";
    private static final String FORMAT_EXC = "IO Exception calling PAF API %s";

    public <T> Try<T> execute(Call<T> call) {
        UUID correlationKey = UUID.randomUUID();
        long startTime = System.currentTimeMillis();
        log.debug(() -> String.format("Paf Request[%s] %s", correlationKey, call.request().toString()));

        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                T body = response.body();
                return Try.success(body);
            } else {
                return handleUnsuccessful(call, response);
            }
        } catch (Exception e) {
            String msg = String.format(FORMAT_EXC, call.request().toString());
            return Try.failure(new PafApiFailure(msg, e));
        }  finally {
            log.debug(() -> {
                long endTime = System.currentTimeMillis();
                return String.format("Paf Response[%s] %s. paf_response_time=%s", correlationKey, call.request().toString(), endTime - startTime);
            });
        }
    }

    private <T> Try<T> handleUnsuccessful(Call<T> call, Response<T> response) {
        String msg = String.format(FORMAT, call.request().toString(), response.code(), response.message());
        if (response.code() == 404) {
            return Try.failure(new PafApiNotFoundFailure(msg));
        } else if (response.code() == 422) {
            return Try.failure(new PafApiValidationFailure(msg));
        } else {
            PafApiFailure failure = new PafApiFailure(msg);
            return Try.failure(failure);
        }
    }
}
