package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

import java.io.Serializable;

@Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Style(init = "with*")
@JsonSerialize(as = ImmutableStatsResponse.class)
public interface StatsResponse extends Serializable {
    Integer count();
    String key();
}
