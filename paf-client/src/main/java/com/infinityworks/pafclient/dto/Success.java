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
@JsonDeserialize(as = ImmutableSuccess.class)
@JsonSerialize(as = ImmutableSuccess.class)
public interface Success {
    @Nullable @JsonProperty("code") String code();
    @Nullable @JsonProperty("http_code") Integer httpCode();
    @Nullable @JsonProperty("message") String message();
}
