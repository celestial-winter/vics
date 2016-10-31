package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.hibernate.validator.constraints.NotEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordVoteRequest {
    @NotEmpty
    private final String wardCode;
    @NotEmpty
    private final String ern;

    @JsonCreator
    public RecordVoteRequest(@JsonProperty("wardCode") String wardCode,
                             @JsonProperty("ern") String ern) {
        this.wardCode = wardCode;
        this.ern = ern;
    }

    public String getWardCode() {
        return wardCode;
    }

    public String getErn() {
        return ern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecordVoteRequest)) return false;
        RecordVoteRequest that = (RecordVoteRequest) o;
        return Objects.equal(wardCode, that.wardCode) &&
                Objects.equal(ern, that.ern);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(wardCode, ern);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("wardCode", wardCode)
                .add("ern", ern)
                .toString();
    }
}
