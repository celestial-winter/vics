package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableIssues.class)
@JsonSerialize(as = ImmutableIssues.class)
public interface Issues {
    @Value.Default
    default Boolean cost() {
        return false;
    }
    @Value.Default
    default Boolean sovereignty() {
        return false;
    }
    @Value.Default
    default @JsonProperty("borders") Boolean border() {
        return false;
    }
}
