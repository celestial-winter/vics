package com.infinityworks.webapp.converter;

import com.infinityworks.pafclient.dto.PafStreetResponse;
import com.infinityworks.webapp.rest.dto.ImmutableStreet;
import com.infinityworks.webapp.rest.dto.Street;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PafToStreetResponseConverter implements Function<PafStreetResponse, Street> {
    @Override
    public Street apply(PafStreetResponse pafStreet) {
        return ImmutableStreet
                .builder()
                .withMainStreet(pafStreet.mainStreet())
                .withPostTown(pafStreet.postTown())
                .withDependentStreet(pafStreet.dependentStreet())
                .withDependentLocality(pafStreet.dependentLocality())
                .withNumVoters(pafStreet.voters())
                .withNumCanvassed(pafStreet.canvassed())
                .withPostcode(pafStreet.postcode())
                .withPriority(pafStreet.priority())
                .withPledged(pafStreet.pledged())
                .withVotedPledges(pafStreet.votedPledges())
                .withNumPostalVoters(pafStreet.postalVoters())
                .build();
    }
}
