package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WardSummary {
    private final String code;
    private final String name;
    private final String constituencyName;

    @JsonCreator
    public WardSummary(@JsonProperty("code") String code,
                       @JsonProperty("name") String name,
                       @JsonProperty("constituencyName") String constituencyName) {
        this.code = code;
        this.name = name;
        this.constituencyName = constituencyName;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getConstituencyName() {
        return constituencyName;
    }
}
