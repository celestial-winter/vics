package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableConstituenciesStats.class)
@JsonSerialize(as = ImmutableConstituenciesStats.class)
public interface ConstituenciesStats {
    @JsonProperty("canvassed") int canvassed();
    @JsonProperty("pledged") int pledged();
    @JsonProperty("voted") StatsVoted voted();
    @JsonProperty("intention") StatsIntention intention();
    @JsonProperty("name") String name();
    @JsonProperty("code") String code();
}
