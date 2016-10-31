package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonSerialize(as = ImmutableLeaderboardStatsResponse.class)
public interface LeaderboardStatsResponse {
    List<StatsResponse> topCanvassers();

    List<StatsResponse> topWards();

    List<StatsResponse> topConstituencies();
}
