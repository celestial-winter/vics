package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

import static java.util.Collections.emptyList;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutablePafStreetResponse.class)
@JsonSerialize(as = ImmutablePafStreetResponse.class)
public interface PafStreetResponse {
    @JsonProperty("main_street") String mainStreet();
    @JsonProperty("post_town") String postTown();
    @JsonProperty("dependent_street") String dependentStreet();
    @JsonProperty("dependent_locality") String dependentLocality();
    @JsonProperty("canvassed") Integer canvassed();
    @JsonProperty("voters") Integer voters();
    @JsonProperty("postal") @Value.Default default int postalVoters() {
        return 0;
    }
    @JsonProperty("postcode") @Value.Default default List<String> postcode() {
        return emptyList();
    }
    @JsonProperty("priority") @Value.Default default int priority() {
        return 0;
    }
    @JsonProperty("voted_pledges") @Value.Default default int votedPledges() {
        return 0;
    }
    @JsonProperty("pledges") @Value.Default default int pledged() {
        return 0;
    }
}
