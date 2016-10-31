package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonSerialize(as = ImmutableAddressLookupRequest.class)
@JsonDeserialize(as = ImmutableAddressLookupRequest.class)
public interface AddressLookupRequest {
    List<String> addresses();
}
