package com.infinityworks.webapp.clients.paf.dto;

import com.google.common.io.Resources;
import com.infinityworks.pafclient.dto.RecordContactRequest;
import org.junit.Test;

import static com.infinityworks.webapp.common.JsonUtil.objectMapper;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RecordContactRequestTest {
    @Test
    public void deserialisesRecordContactRequest() throws Exception {
        RecordContactRequest contactRequest = objectMapper.readValue(
                Resources.getResource("json/paf-record-contact.json"), RecordContactRequest.class);

        assertThat(contactRequest.contactType(), is("canvass"));
        assertThat(contactRequest.userId(), is("63f93970-d065-4fbb-8b9c-941e27ea53dc"));
        assertThat(contactRequest.flags().deceased(), is(false));
        assertThat(contactRequest.flags().hasPV(), is(true));
        assertThat(contactRequest.flags().wantsPV(), is(false));
        assertThat(contactRequest.flags().inaccessible(), is(false));
        assertThat(contactRequest.flags().lift(), is(false));
        assertThat(contactRequest.flags().poster(), is(false));
        assertThat(contactRequest.issues().border(), is(false));
        assertThat(contactRequest.issues().cost(), is(false));
        assertThat(contactRequest.issues().sovereignty(), is(false));
    }
}
