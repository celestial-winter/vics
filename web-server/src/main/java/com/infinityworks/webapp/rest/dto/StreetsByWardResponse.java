package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.infinityworks.pafclient.dto.Stats;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonSerialize(as = ImmutableStreetsByWardResponse.class)
@JsonDeserialize(as = ImmutableStreetsByWardResponse.class)
public interface StreetsByWardResponse {
    @JsonProperty("streets") List<Street> streets();
    @JsonProperty("stats")
    Stats stats();
}
