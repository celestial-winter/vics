package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutablePafStreetRequest.class)
@JsonSerialize(as = ImmutablePafStreetRequest.class)
public interface PafStreetRequest {
    @JsonProperty("main_street") @Value.Default default String mainStreet() {return "";}
    @JsonProperty("post_town") @Value.Default default String postTown() {return "";}
    @JsonProperty("dependent_street") @Value.Default default String dependentStreet() {return "";}
    @JsonProperty("dependent_locality") @Value.Default default String dependentLocality() {return "";}
}
