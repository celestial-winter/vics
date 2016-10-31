package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableInfo.class)
@JsonSerialize(as = ImmutableInfo.class)
public interface Info {
    @Value.Default @JsonProperty("phone") default String telephone() {
        return "";
    }
    @Value.Default @JsonProperty("email") default String email() {
        return "";
    }
}
