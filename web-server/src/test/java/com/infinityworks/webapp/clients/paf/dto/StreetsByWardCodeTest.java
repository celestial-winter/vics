package com.infinityworks.webapp.clients.paf.dto;

import com.google.common.io.Resources;
import com.infinityworks.pafclient.dto.PafStreetResponse;
import com.infinityworks.pafclient.dto.StreetsResponse;
import org.junit.Test;

import static com.infinityworks.webapp.common.JsonUtil.objectMapper;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StreetsByWardCodeTest {
    @Test
    public void deserialisesStreetsByWardCodeResponse() throws Exception {
        StreetsResponse streetsResponse = objectMapper.readValue(
                Resources.getResource("json/paf-streets.json"), StreetsResponse.class);

        PafStreetResponse pafStreet = streetsResponse.response().get(0);
        assertThat(pafStreet.canvassed(), is(0));
        assertThat(pafStreet.voters(), is(0));
        assertThat(pafStreet.postTown(), is("KINGSTON UPON THAMES"));
        assertThat(pafStreet.mainStreet(), is("Acre Road"));
    }
}
