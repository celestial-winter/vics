package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

@Immutable
@Style(init = "with*")
@JsonSerialize(as = ImmutableOverallStats.class)
public interface OverallStats {
    int canvassed();
    int voted();
    int pledges();
    int pledgesVoted();
}
