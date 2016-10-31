package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableVoter.class)
@JsonSerialize(as = ImmutableVoter.class)
public interface Voter {
    @Value.Default @JsonProperty("prefix") default String pollingDistrict() {
        return "";
    }
    @Value.Default @JsonProperty("number") default String electorNumber() {
        return "";
    }
    @Value.Default @JsonProperty("suffix") default String electorSuffix() {
        return "";
    }
    @Value.Default @JsonProperty("full_name") default String fullName() {
        return "";
    }
    @Nullable @JsonProperty("voting") Voting voting();
    @Nullable @JsonProperty("flags") Flags flags();
    @Nullable @JsonProperty("issues") Issues issues();
    @Nullable @JsonProperty("info") Info info();
}
