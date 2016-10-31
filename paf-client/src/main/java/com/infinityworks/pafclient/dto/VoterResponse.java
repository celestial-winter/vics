package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*", jdkOnly = true)
@JsonDeserialize(as = ImmutableVoterResponse.class)
@JsonSerialize(as = ImmutableVoterResponse.class)
public interface VoterResponse {
    @JsonProperty("response") List<VoterSummary> voters();
}
