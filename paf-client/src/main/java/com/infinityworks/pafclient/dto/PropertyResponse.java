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
@JsonDeserialize(as = ImmutablePropertyResponse.class)
@JsonSerialize(as = ImmutablePropertyResponse.class)
public interface PropertyResponse {
    @JsonProperty("response") List<List<Property>> response();
}
