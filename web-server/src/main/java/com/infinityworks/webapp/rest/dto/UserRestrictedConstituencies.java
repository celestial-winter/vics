package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.infinityworks.webapp.domain.Constituency;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRestrictedConstituencies {
    private Set<Constituency> constituencies;

    @JsonCreator
    public UserRestrictedConstituencies(@JsonProperty("constituencies") Set<Constituency> constituencies) {
        this.constituencies = constituencies;
    }

    public Set<Constituency> getConstituencies() {
        return constituencies;
    }
}
