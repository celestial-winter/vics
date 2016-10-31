package com.infinityworks.webapp.converter;

import com.infinityworks.pafclient.dto.ImmutablePafStreetResponse;
import com.infinityworks.webapp.rest.dto.Street;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PafToStreetResponseConverterTest {
    @Test
    public void mapsTheStreetResponseFromPaf() {
        PafToStreetResponseConverter underTest = new PafToStreetResponseConverter();

        Street streetResponse = underTest.apply(ImmutablePafStreetResponse.builder()
                .withDependentStreet("dependentStreet")
                .withMainStreet("mainStreet")
                .withCanvassed(10)
                .withVoters(20)
                .withDependentLocality("dependentLocality")
                .withPostTown("postTown")
                .withPriority(2)
                .build()
        );

        assertThat(streetResponse.mainStreet(), is("mainStreet"));
        assertThat(streetResponse.dependentStreet(), is("dependentStreet"));
        assertThat(streetResponse.dependentLocality(), is("dependentLocality"));
        assertThat(streetResponse.numCanvassed(), is(10));
        assertThat(streetResponse.numVoters(), is(20));
        assertThat(streetResponse.postTown(), is("postTown"));
        assertThat(streetResponse.priority(), is(2));
    }
}
