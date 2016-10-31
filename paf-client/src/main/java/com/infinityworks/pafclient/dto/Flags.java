package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableFlags.class)
@JsonSerialize(as = ImmutableFlags.class)
public interface Flags {
    @Value.Default @JsonProperty("has_postal")
    default Boolean hasPV() {
        return false;
    }
    @Value.Default @JsonProperty("wants_postal")
    default Boolean wantsPV() {
        return false;
    }
    @Value.Default
    default Boolean lift() {
        return false;
    }
    @Value.Default
    default Boolean deceased() {
        return false;
    }
    @Value.Default
    default Boolean inaccessible() {
        return false;
    }
    @Value.Default
    default @JsonProperty("wants_poster") Boolean poster() {
        return false;
    }
}
