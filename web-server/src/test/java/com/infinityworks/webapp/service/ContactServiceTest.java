package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.pafclient.PafClient;
import com.infinityworks.pafclient.PafRequestExecutor;
import com.infinityworks.pafclient.dto.ImmutableRecordContactResponse;
import com.infinityworks.pafclient.dto.RecordContactResponse;
import com.infinityworks.webapp.converter.RecordContactToPafConverter;
import com.infinityworks.webapp.domain.Ern;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.domain.Ward;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.rest.dto.RecordContactRequest;
import com.infinityworks.webapp.testsupport.mocks.CallStub;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;

import java.util.UUID;

import static com.google.common.collect.Sets.newHashSet;
import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static com.infinityworks.webapp.testsupport.builder.WardBuilder.ward;
import static com.infinityworks.webapp.testsupport.builder.downstream.RecordContactRequestBuilder.recordContactRequest;
import static com.infinityworks.webapp.testsupport.matcher.TryFailureMatcher.isFailure;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ContactServiceTest {

    private RecordContactService underTest;
    private PafClient pafClient;
    private WardService wardService;
    private RecordContactToPafConverter recordContactToPafConverter = new RecordContactToPafConverter();

    @Before
    public void setUp() throws Exception {
        pafClient = mock(PafClient.class);
        wardService = mock(WardService.class);
    }

    @Test
    public void recordsContact() throws Exception {
        PafRequestExecutor requestExecutor = new PafRequestExecutor() {};
        RecordContactLogService logService = mock(RecordContactLogService.class);
        underTest = new RecordContactService(wardService, recordContactToPafConverter, requestExecutor, pafClient, logService);

        Ward earlsdon = ward().withWardCode("E05001221").build();
        RecordContactRequest request = recordContactRequest().build();
        User user = user()
                .withWriteAccess(true)
                .withWards(newHashSet(earlsdon))
                .build();
        given(wardService.getByCode("E05001221", user)).willReturn(Try.success(earlsdon));
        com.infinityworks.pafclient.dto.RecordContactRequest contactRecord = recordContactToPafConverter.apply(user, request);
        Call<RecordContactResponse> success = CallStub.success(ImmutableRecordContactResponse.builder().withId(UUID.randomUUID()).withErn("E05001221-PD-123-4").build());
        given(pafClient.recordContact("E05001221-PD-123-4", contactRecord)).willReturn(success);

        Try<com.infinityworks.webapp.rest.dto.RecordContactResponse> contact = underTest.recordContact(user, Ern.valueOf("E05001221-PD-123-4"), request);

        assertThat(contact.isSuccess(), is(true));
    }

    @Test
    public void recordsContactFailsIfUserDoesNotHaveWriteAccess() throws Exception {
        PafRequestExecutor requestExecutor = new PafRequestExecutor() {};
        RecordContactLogService logService = mock(RecordContactLogService.class);
        underTest = new RecordContactService(wardService, recordContactToPafConverter, requestExecutor, pafClient, logService);

        Ward earlsdon = ward().withWardCode("E05001221").build();
        RecordContactRequest request = recordContactRequest().build();
        User user = user()
                .withWriteAccess(false)
                .withWards(newHashSet(earlsdon))
                .build();
        given(wardService.getByCode("E05001221", user)).willReturn(Try.success(earlsdon));

        Try<com.infinityworks.webapp.rest.dto.RecordContactResponse> contact = underTest.recordContact(user, Ern.valueOf("E05001221-PD-123-1"), request);

        assertThat(contact, isFailure(instanceOf(NotAuthorizedFailure.class)));
    }

    @Test
    public void failsToRecordContactIfUserDoesNotHaveWardPermission() throws Exception {
        PafRequestExecutor requestExecutor = new PafRequestExecutor() {};
        RecordContactLogService logService = mock(RecordContactLogService.class);
        underTest = new RecordContactService(wardService, recordContactToPafConverter, requestExecutor, pafClient, logService);

        RecordContactRequest request = recordContactRequest().build();
        User user = user()
                .withWriteAccess(true)
                .build();
        given(wardService.getByCode("E05001221", user)).willReturn(Try.failure(new NotAuthorizedFailure("forbidden")));

        Try<com.infinityworks.webapp.rest.dto.RecordContactResponse> contact = underTest.recordContact(user, Ern.valueOf("E05001221-PD-123-1"), request);

        assertThat(contact, isFailure(instanceOf(NotAuthorizedFailure.class)));
    }
}
