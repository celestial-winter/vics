package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssociateUserConstituency {
    private final String username;
    private final String constituencyCode;

    @JsonCreator
    public AssociateUserConstituency(@JsonProperty("username") String username,
                                     @JsonProperty("constituencyCode") String constituencyCode) {
        this.username = username;
        this.constituencyCode = constituencyCode;
    }

    public String getUsername() {
        return username;
    }

    public String getConstituencyCode() {
        return constituencyCode;
    }
}
