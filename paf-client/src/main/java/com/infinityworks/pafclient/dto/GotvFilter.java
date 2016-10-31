package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableGotvFilter.class)
@JsonSerialize(as = ImmutableGotvFilter.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface GotvFilter {
    @Nullable GotvVotingCriteria voting();
    @Nullable GotvFilterFlags flags();
}
