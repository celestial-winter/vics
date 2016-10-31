package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonSerialize(as = ImmutableStreet.class)
@JsonDeserialize(as = ImmutableStreet.class)
public interface Street {
    @JsonProperty("mainStreet") String mainStreet();
    @JsonProperty("postTown") String postTown();
    @JsonProperty("dependentStreet") String dependentStreet();
    @JsonProperty("dependentLocality") String dependentLocality();
    @JsonProperty("numVoters") Integer numVoters();
    @JsonProperty("numCanvassed") Integer numCanvassed();
    @JsonProperty("postcode") Object postcode();
    @JsonProperty("priority") Integer priority();
    @JsonProperty("pledged") Integer pledged();
    @JsonProperty("votedPledges") Integer votedPledges();
    @JsonProperty("numPostalVoters") Integer numPostalVoters();
}
