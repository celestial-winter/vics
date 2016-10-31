package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*", jdkOnly = true)
@JsonDeserialize(as = ImmutableGotvVoterRequest.class)
@JsonSerialize(as = ImmutableGotvVoterRequest.class)
public interface GotvVoterRequest {
    List<PafStreetRequest> streets();
    GotvFilter filter();
}
