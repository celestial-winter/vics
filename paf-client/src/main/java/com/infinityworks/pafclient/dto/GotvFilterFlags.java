package com.infinityworks.pafclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonDeserialize(as = ImmutableGotvFilterFlags.class)
@JsonSerialize(as = ImmutableGotvFilterFlags.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface GotvFilterFlags {
    @Nullable @JsonProperty("has_postal") Boolean hasPV();
    @Nullable @JsonProperty("wants_postal") Boolean wantsPV();
    @Nullable Boolean lift();
    @Nullable Boolean inaccessible();
    @Nullable @JsonProperty("wants_poster") Boolean poster();
    @Nullable @JsonProperty("deceased") Boolean deceased();
    @Nullable @JsonProperty("phone") Boolean phone();
    @Nullable @JsonProperty("voted") Boolean voted();
}
