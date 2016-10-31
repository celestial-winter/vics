package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.io.Serializable;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableStatsIntention.class)
@JsonSerialize(as = ImmutableStatsIntention.class)
public interface StatsIntention extends Serializable {
    @JsonProperty("1") int remain();
    @JsonProperty("2") int probablyRemain();
    @JsonProperty("3") int undecided();
    @JsonProperty("4") int probablyLeave();
    @JsonProperty("5") int leave();
}
