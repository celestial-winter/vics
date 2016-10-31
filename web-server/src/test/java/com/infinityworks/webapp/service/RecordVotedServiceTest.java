package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.pafclient.PafClient;
import com.infinityworks.pafclient.PafRequestExecutor;
import com.infinityworks.pafclient.dto.*;
import com.infinityworks.webapp.domain.Ern;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.domain.Ward;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.rest.dto.RecordVoteResponse;
import com.infinityworks.webapp.testsupport.mocks.CallStub;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;

import java.util.UUID;

import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static com.infinityworks.webapp.testsupport.builder.WardBuilder.ward;
import static com.infinityworks.webapp.testsupport.matcher.TryFailureMatcher.isFailure;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class RecordVotedServiceTest {

    private RecordVotedService underTest;
    private PafClient pafClient;
    private WardService wardService;

    @Before
    public void setUp() throws Exception {
        pafClient = mock(PafClient.class);
        wardService = mock(WardService.class);
        underTest = new RecordVotedService(wardService, pafClient, new PafRequestExecutor(), mock(Auditor.class));
    }

    @Test
    public void recordsAVote() throws Exception {
        User user = user().withWriteAccess(true).build();
        Ern recordVote = Ern.valueOf("E05001221-PD-123-1");
        Ward ward = ward().withWardCode("E05001221").build();
        Call<RecordVotedResponse> call = CallStub.success(ImmutableRecordVotedResponse.builder().withSuccess(
                ImmutableSuccess.builder().withCode("GEN-SUCCESS").withHttpCode(200).withMessage("Vote recorded").build()).build());
        given(pafClient.recordVote("E05001221-PD-123-1")).willReturn(call);
        given(wardService.getByCode(recordVote.getWardCode(), user)).willReturn(Try.success(ward));

        Try<RecordVoteResponse> recordVoteResponse = underTest.recordVote(user, recordVote);

        assertThat(recordVoteResponse.isSuccess(), is(true));
    }

    @Test
    public void undosAWontVote() throws Exception {
        User user = user().withWriteAccess(true).build();
        Ern recordVote = Ern.valueOf("E05001221-PD-123-1");
        Ward ward = ward().withWardCode("E05001221").build();
        UUID contactId = UUID.randomUUID();
        ImmutableSuccess success = ImmutableSuccess.builder().withCode("GEN-SUCCESS").withHttpCode(200).withMessage("Vote recorded").build();
        Call<DeleteContactResponse> call = CallStub.success(ImmutableDeleteContactResponse.builder().withSuccess(
                success).build());
        given(pafClient.deleteContact("E05001221-PD-123-1", contactId)).willReturn(call);
        given(wardService.getByCode(recordVote.getWardCode(), user)).willReturn(Try.success(ward));

        Try<DeleteContactResponse> response = underTest.undoWontVote(user, recordVote, contactId);

        assertThat(response.isSuccess(), is(true));
        assertThat(response.get().success(), is(success));
    }

    @Test
    public void failsToRecordVoteIfUserDoesNotHaveWardPermission() throws Exception {
        User user = user().withWriteAccess(true).build();
        Ern recordVote = Ern.valueOf("E05001221-PD-123-1");
        Call<RecordVotedResponse> call = CallStub.success(ImmutableRecordVotedResponse.builder().withSuccess(
                ImmutableSuccess.builder().withCode("GEN-SUCCESS").withHttpCode(200).withMessage("Vote recorded").build()).build());
        given(pafClient.recordVote(recordVote.longForm())).willReturn(call);
        given(wardService.getByCode(recordVote.getWardCode(), user)).willReturn(Try.failure(new NotAuthorizedFailure("failure")));

        Try<RecordVoteResponse> recordVoteResponse = underTest.recordVote(user, recordVote);

        assertThat(recordVoteResponse, isFailure(instanceOf(NotAuthorizedFailure.class)));
    }

    @Test
    public void failsToRecordVoteIfUserDoesNotHaveWriteAccess() throws Exception {
        User user = user().withWriteAccess(false).build();
        Ern recordVote = Ern.valueOf("E05001221-PD-123-1");

        Try<RecordVoteResponse> recordVoteResponse = underTest.recordVote(user, recordVote);

        assertThat(recordVoteResponse, isFailure(instanceOf(NotAuthorizedFailure.class)));
    }
}
