package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.infinityworks.pafclient.dto.ConstituenciesStats;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonSerialize(as = ImmutableConstituenciesStatsResponse.class)
@JsonDeserialize(as = ImmutableConstituenciesStatsResponse.class)
public interface ConstituenciesStatsResponse {
    ConstituenciesStats stats();
    String region();
}
