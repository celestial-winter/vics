package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Flags {
    @Min(0)
    @Max(5)
    private final Integer intentionFrom;

    @Min(0)
    @Max(5)
    private final Integer intentionTo;

    @Min(0)
    @Max(5)
    private final Integer likelihoodFrom;

    @Min(0)
    @Max(5)
    private final Integer likelihoodTo;

    private final Boolean hasPV;
    private final Boolean wantsPv;
    private final Boolean needsLift;
    private final Boolean inaccessible;
    private final Boolean poster;
    private final Boolean telephone;
    private final Boolean email;

    @JsonCreator
    public Flags(@JsonProperty("intentionFrom") Integer intentionFrom,
                 @JsonProperty("intentionTo") Integer intentionTo,
                 @JsonProperty("likelihoodFrom") Integer likelihoodFrom,
                 @JsonProperty("likelihoodTo") Integer likelihoodTo,
                 @JsonProperty("hasPV") Boolean hasPV,
                 @JsonProperty("wantsPV") Boolean wantsPv,
                 @JsonProperty("lift") Boolean needsLift,
                 @JsonProperty("inaccessible") Boolean inaccessible,
                 @JsonProperty("poster") Boolean poster,
                 @JsonProperty("email") Boolean email,
                 @JsonProperty("telephone") Boolean telephone) {
        this.intentionFrom = intentionFrom;
        this.intentionTo = intentionTo;
        this.likelihoodFrom = likelihoodFrom;
        this.likelihoodTo = likelihoodTo;
        this.hasPV = hasPV;
        this.wantsPv = wantsPv;
        this.needsLift = needsLift;
        this.inaccessible = inaccessible;
        this.poster = poster;
        this.email = email;
        this.telephone = telephone;
    }

    public Integer getIntentionFrom() {
        return intentionFrom;
    }

    public Integer getIntentionTo() {
        return intentionTo;
    }

    public Integer getLikelihoodFrom() {
        return likelihoodFrom;
    }

    public Integer getLikelihoodTo() {
        return likelihoodTo;
    }

    public Boolean getHasPV() {
        return hasPV;
    }

    public Boolean getWantsPv() {
        return wantsPv;
    }

    public Boolean getNeedsLift() {
        return needsLift;
    }

    public Boolean getInaccessible() {
        return inaccessible;
    }

    public Boolean getPoster() {
        return poster;
    }

    public Boolean getTelephone() {
        return telephone;
    }

    public Boolean getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("intentionFrom", intentionFrom)
                .add("intentionTo", intentionTo)
                .add("likelihoodFrom", likelihoodFrom)
                .add("likelihoodTo", likelihoodTo)
                .add("hasPV", hasPV)
                .add("wantsPv", wantsPv)
                .add("needsLift", needsLift)
                .add("inaccessible", inaccessible)
                .add("poster", poster)
                .add("telephone", telephone)
                .add("email", email)
                .toString();
    }
}
