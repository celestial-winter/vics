package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

import java.time.LocalDateTime;
import java.util.List;

@Immutable
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableAllConstituenciesStatsResponse.class)
public interface AllConstituenciesStatsResponse {
    LocalDateTime updated();
    List<ConstituenciesStats> constituencies();
}
