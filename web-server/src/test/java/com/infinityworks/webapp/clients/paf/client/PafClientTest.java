package com.infinityworks.webapp.clients.paf.client;

import com.google.common.io.Resources;
import com.infinityworks.pafclient.dto.DeleteContactResponse;
import com.infinityworks.pafclient.dto.SearchVoterResponse;
import org.junit.Test;

import static com.infinityworks.webapp.common.JsonUtil.objectMapper;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PafClientTest {
    @Test
    public void deserialisesDeleteContactRequest() throws Exception {
        DeleteContactResponse response = objectMapper.readValue(
                Resources.getResource("json/paf-delete-contact-success.json"), DeleteContactResponse.class);

        assertThat(response.success().message(), is("Contact record deleted"));
        assertThat(response.success().code(), is("GEN-SUCCESS"));
        assertThat(response.success().httpCode(), is(200));
    }

    @Test
    public void deserialisesSearchVoterResponse() throws Exception {
        SearchVoterResponse[] response = objectMapper.readValue(
                Resources.getResource("json/paf-search-voter.json"), SearchVoterResponse[].class);

        SearchVoterResponse first = response[0];
        assertThat(first.surname(), is("McCall"));
        assertThat(first.firstName(), is("John B"));
        assertThat(first.fullName(), is("McCall, John B"));
        assertThat(first.ern(), is("E050097474-LFF-305-0"));
        assertThat(first.address().postTown(), is("Coventry"));
        assertThat(first.address().postcode(), is("CV4 6PL"));
    }
}