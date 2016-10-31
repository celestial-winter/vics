package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssociateUserWard {
    private final String username;
    private final String wardCode;

    @JsonCreator
    public AssociateUserWard(@JsonProperty("username") String username,
                             @JsonProperty("wardCode") String wardCode) {
        this.username = username;
        this.wardCode = wardCode;
    }

    public String getUsername() {
        return username;
    }

    public String getWardCode() {
        return wardCode;
    }
}
