package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.clients.pdfserver.PdfClient;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.domain.Ward;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.rest.dto.ElectorsByStreetsRequest;
import org.junit.Before;
import org.junit.Test;

import static com.infinityworks.webapp.testsupport.Fixtures.streetsRequest;
import static com.infinityworks.webapp.testsupport.builder.WardBuilder.ward;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CanvassCardServiceTest {
    private CanvassCardService underTest;
    private WardService wardService;
    private PdfClient pdfClient;
    private Auditor auditor;

    @Before
    public void setUp() throws Exception {
        wardService = mock(WardService.class);
        pdfClient = mock(PdfClient.class);
        auditor = mock(Auditor.class);
        underTest = new CanvassCardService(wardService, pdfClient, auditor);
    }

    @Test
    public void returnsNotAuthorizedIfNoWardPermission() throws Exception {
        User user = mock(User.class);
        given(wardService.getByCode("code", user))
                .willReturn(Try.failure(new NotAuthorizedFailure("notAuthorized")));

        Try<byte[]> canvassCard = underTest.generateCanvassCard(null, "code", user);

        assertThat(canvassCard.isFailure(), is(true));
        verifyZeroInteractions(pdfClient);
    }

    @Test
    public void returnsTheCanvassCard() throws Exception {
        User user = mock(User.class);
        Ward ward = ward().withWardCode("code").build();
        byte[] pdfContent = "content".getBytes();
        given(wardService.getByCode(ward.getCode(), user)).willReturn(Try.success(ward));
        given(pdfClient.requestCanvassCard(any())).willReturn(Try.success(pdfContent));

        ElectorsByStreetsRequest request = streetsRequest();
        Try<byte[]> canvassCard = underTest.generateCanvassCard(request, ward.getCode(), user);

        assertThat(canvassCard.get(), is(pdfContent));
    }
}