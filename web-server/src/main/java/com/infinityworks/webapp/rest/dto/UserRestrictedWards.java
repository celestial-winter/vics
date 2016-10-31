package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.infinityworks.webapp.domain.Ward;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRestrictedWards {
    private Set<Ward> wards;

    @JsonCreator
    public UserRestrictedWards(@JsonProperty("wards") Set<Ward> wards) {
        this.wards = wards;
    }

    public Set<Ward> getWards() {
        return wards;
    }
}
