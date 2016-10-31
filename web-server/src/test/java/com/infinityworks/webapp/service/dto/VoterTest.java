package com.infinityworks.webapp.service.dto;

import com.google.common.io.Resources;
import com.infinityworks.pafclient.dto.Voter;
import org.junit.Test;

import static com.infinityworks.webapp.common.JsonUtil.objectMapper;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VoterTest {
    @Test
    public void deserialisesAProperty() throws Exception {
        Voter voter = objectMapper.readValue(
                Resources.getResource("json/paf-voter.json"), Voter.class);

        assertThat(voter.fullName(), is("Deaux, John"));
        assertThat(voter.pollingDistrict(), is("AB"));
        assertThat(voter.electorNumber(), is("01"));
        assertThat(voter.electorSuffix(), is("1"));
        assertThat(voter.flags().deceased(), is(false));
        assertThat(voter.flags().inaccessible(), is(false));
        assertThat(voter.flags().hasPV(), is(false));
        assertThat(voter.flags().wantsPV(), is(false));
        assertThat(voter.issues().cost(), is(false));
        assertThat(voter.issues().sovereignty(), is(true));
        assertThat(voter.issues().border(), is(false));
    }
}
