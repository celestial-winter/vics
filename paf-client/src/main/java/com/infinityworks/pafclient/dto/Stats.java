package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableStats.class)
@JsonSerialize(as = ImmutableStats.class)
public interface Stats {
    @JsonProperty("voters") int voters();
    @JsonProperty("canvassed") int canvassed();
    @Value.Default @JsonProperty("pledges") default int pledged() {return 0;}
    @Value.Default @JsonProperty("voted_pledges") default int votedPledges() {return 0;}
}
