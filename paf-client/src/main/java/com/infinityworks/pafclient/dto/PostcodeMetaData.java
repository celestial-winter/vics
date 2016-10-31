package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutablePostcodeMetaData.class)
public interface PostcodeMetaData {
    @JsonProperty("electoral_ward_name") String wardName();
    @JsonProperty("electoral_ward_code") String wardCode();
    @JsonProperty("parliamentary_constituency_name") String constituencyName();
    @JsonProperty("parliamentary_constituency_code") String constituencyCode();
    @JsonProperty("latitude_etrs89") String latitude();
    @JsonProperty("longitude_etrs89") String longitude();
}
