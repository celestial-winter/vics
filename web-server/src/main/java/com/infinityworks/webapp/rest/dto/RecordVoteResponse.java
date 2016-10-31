package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

import javax.annotation.Nullable;

@Immutable
@Value.Style(init = "with*")
@JsonSerialize(as = ImmutableRecordVoteResponse.class)
@JsonDeserialize(as = ImmutableRecordVoteResponse.class)
public interface RecordVoteResponse {
    String wardCode();
    String ern();
    @Nullable String id();
}
