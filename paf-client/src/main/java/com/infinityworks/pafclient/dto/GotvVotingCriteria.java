package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableGotvVotingCriteria.class)
@JsonSerialize(as = ImmutableGotvVotingCriteria.class)
public interface GotvVotingCriteria {
    Range likelihood();
    Range intention();
}
