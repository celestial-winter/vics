package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.clients.pdfserver.PdfClient;
import com.infinityworks.webapp.clients.pdfserver.dto.GeneratePdfRequest;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.domain.Ward;
import com.infinityworks.webapp.rest.dto.ElectorsByStreetsRequest;
import com.infinityworks.webapp.rest.dto.Flags;
import com.infinityworks.webapp.rest.dto.StreetRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static com.infinityworks.webapp.testsupport.builder.ConstituencyBuilder.constituency;
import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static com.infinityworks.webapp.testsupport.builder.WardBuilder.ward;
import static com.infinityworks.webapp.testsupport.builder.downstream.FlagsBuilder.flags;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LabelServiceTest {
    private LabelService underTest;
    private WardService wardService;
    private PdfClient pdfClient;

    @Before
    public void setUp() throws Exception {
        wardService = mock(WardService.class);
        pdfClient = mock(PdfClient.class);
        underTest = new LabelService(wardService, pdfClient);
    }

    @Test
    public void generatesTheAddressLabels() throws Exception {
        String wardCode = "E05000403";
        String wardName = "Canbury";
        String constituencyName = "Richmond Park";
        User user = user().build();
        ArgumentCaptor<GeneratePdfRequest> pdfRequestCaptor = ArgumentCaptor.forClass(GeneratePdfRequest.class);

        Flags flags = flags().build();
        ElectorsByStreetsRequest request = new ElectorsByStreetsRequest(
                singletonList(new StreetRequest("mainStreet", "postTown", "dependentStreet", "dependentLocality")),
                flags);

        Ward ward = ward().withWardName(wardName).withWardCode(wardCode).withConstituency(constituency().withName(constituencyName).build()).build();
        given(wardService.getByCode(wardCode, user)).willReturn(Try.success(ward));
        when(pdfClient.requestAddressLabels(pdfRequestCaptor.capture())).thenReturn(Try.success("content".getBytes()));

        Try<byte[]> labels = underTest.generateAddressLabelsForPostalVoters(request, wardCode, user);

        assertThat(labels.get(), is("content".getBytes()));
        assertThat(labels.isSuccess(), is(true));

        GeneratePdfRequest pdfRequest = pdfRequestCaptor.getValue();
        assertThat(pdfRequest.getFlags(), is(flags));
        assertThat(pdfRequest.getInfo().getWardCode(), is(wardCode));
        assertThat(pdfRequest.getInfo().getWardName(), is(wardName));
        assertThat(pdfRequest.getInfo().getConstituencyName(), is(constituencyName));
    }
}
