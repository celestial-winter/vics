package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.pafclient.PafClient;
import com.infinityworks.pafclient.PafRequestExecutor;
import com.infinityworks.pafclient.dto.StreetsResponse;
import com.infinityworks.webapp.converter.PafToStreetResponseConverter;
import com.infinityworks.webapp.rest.dto.ImmutableStreetsByWardResponse;
import com.infinityworks.webapp.rest.dto.Street;
import com.infinityworks.webapp.rest.dto.StreetsByWardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class PafAddressService {
    private final PafClient pafClient;
    private final PafToStreetResponseConverter pafToStreetConverter;
    private final PafRequestExecutor responseHandler;

    @Autowired
    public PafAddressService(PafClient pafClient,
                             PafToStreetResponseConverter pafToStreetConverter,
                             PafRequestExecutor responseHandler) {
        this.pafClient = pafClient;
        this.pafToStreetConverter = pafToStreetConverter;
        this.responseHandler = responseHandler;
    }

    public Try<StreetsByWardResponse> getStreetsByWard(String wardCode) {
        Call<StreetsResponse> call = pafClient.streetsByWardCode(wardCode);
        return responseHandler.execute(call).map(str -> {
            List<Street> streets = str.response()
                    .stream()
                    .map(pafToStreetConverter)
                    .collect(toList());

            return ImmutableStreetsByWardResponse.builder()
                    .withStats(str.stats())
                    .withStreets(streets)
                    .build();
        });
    }
}
