package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableGeneratePasswordFromTokenRequest.class)
@JsonSerialize(as = ImmutableGeneratePasswordFromTokenRequest.class)
public interface GeneratePasswordFromTokenRequest {
    String username();
    String token();
}
