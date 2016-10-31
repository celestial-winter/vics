package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableRecordContactRequest.class)
@JsonSerialize(as = ImmutableRecordContactRequest.class)
public interface RecordContactRequest {
    @JsonProperty("contact_type") String contactType();
    @JsonProperty("activist_id") String userId();
    Voting voting();

    @Value.Default default Flags flags() {
        return ImmutableFlags.builder().build();
    }

    @Value.Default
    default Issues issues() {
        return ImmutableIssues.builder().build();
    }

    @Nullable @Value.Default
    default Info info() {
        return ImmutableInfo.builder().build();
    }
}
