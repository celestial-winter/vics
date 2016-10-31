package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

import java.time.LocalDateTime;
import java.util.List;

@Immutable
@JsonSerialize(as = ImmutableStatsOverview.class)
@Style(init = "with*")
public interface StatsOverview {
    List<ConstituenciesStatsResponse> constituencies();
    OverallStats total();
    LocalDateTime updated();
}
