package com.infinityworks.webapp.converter;

import com.infinityworks.pafclient.dto.PafStreetResponse;
import com.infinityworks.webapp.rest.dto.StreetRequest;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PafToStreetRequestConverter implements Function<PafStreetResponse, StreetRequest> {
    @Override
    public StreetRequest apply(PafStreetResponse pafStreet) {
        return new StreetRequest(
                pafStreet.mainStreet(),
                pafStreet.postTown(),
                pafStreet.dependentStreet(),
                pafStreet.dependentLocality());
    }
}
