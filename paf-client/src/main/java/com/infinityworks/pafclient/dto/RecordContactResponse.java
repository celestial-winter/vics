package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableRecordContactResponse.class)
@JsonSerialize(as = ImmutableRecordContactResponse.class)
public interface RecordContactResponse {
    @JsonProperty("ern") String ern();
    @JsonProperty("id") UUID id();
}
