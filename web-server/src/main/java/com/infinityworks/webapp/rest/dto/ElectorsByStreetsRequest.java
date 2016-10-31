package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElectorsByStreetsRequest {
    @Valid
    @NotNull
    @Size(min = 1)
    private final List<StreetRequest> streets;

    // optional (used for gotv requests)
    @Valid
    private final Flags flags;

    @JsonCreator
    public ElectorsByStreetsRequest(@JsonProperty("streets") List<StreetRequest> streets,
                                    @JsonProperty("flags") Flags flags) {
        this.streets = streets;
        this.flags = flags;
    }

    public List<StreetRequest> getStreets() {
        return streets;
    }

    public Flags getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("streets", streets)
                .add("flags", flags)
                .toString();
    }
}
