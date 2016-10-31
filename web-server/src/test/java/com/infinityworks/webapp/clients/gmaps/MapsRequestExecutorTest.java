package com.infinityworks.webapp.clients.gmaps;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.error.MapsApiFailure;
import com.infinityworks.webapp.error.MapsApiNotFoundFailure;
import com.infinityworks.webapp.testsupport.mocks.CallStub;
import org.junit.Before;
import org.junit.Test;

import static com.infinityworks.webapp.testsupport.matcher.TryFailureMatcher.isFailure;
import static com.infinityworks.webapp.testsupport.matcher.TrySuccessMatcher.isSuccess;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class MapsRequestExecutorTest {
    private MapsRequestExecutor underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new MapsRequestExecutor();
    }

    @Test
    public void returnsTheRequestContent() throws Exception {
        String content = "data";
        CallStub<String> request = CallStub.success(content);

        Try<String> response = underTest.execute(request);

        assertThat(response, isSuccess(equalTo(content)));
    }

    @Test
    public void returnsAWrappedMapsApiFailureIfTheRequestReturnsServerError() throws Exception {
        CallStub<String> request = CallStub.serverError();

        Try<String> response = underTest.execute(request);

        assertThat(response.isFailure(), is(true));
        assertThat(response, isFailure(instanceOf(MapsApiFailure.class)));
    }

    @Test
    public void returnsAWrappedMapsApiNotFoundFailureIfTheRequestReturnsNotFound() throws Exception {
        CallStub<String> request = CallStub.notFound();

        Try<String> response = underTest.execute(request);

        assertThat(response.isFailure(), is(true));
        assertThat(response, isFailure(instanceOf(MapsApiNotFoundFailure.class)));
    }
}