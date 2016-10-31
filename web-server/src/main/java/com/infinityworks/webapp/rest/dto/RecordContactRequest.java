package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.infinityworks.common.lang.StringExtras.nullToEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordContactRequest {

    @NotNull
    @Min(0)
    @Max(5)
    private final Integer intention;
    @NotNull
    @Min(0)
    @Max(5)
    private final Integer likelihood;
    @NotNull
    private final Boolean cost;
    @NotNull
    private final Boolean sovereignty;
    @NotNull
    private final Boolean border;
    @NotNull
    private final Boolean lift;
    @NotNull
    private final Boolean hasVoted;
    @NotNull
    private final Boolean hasPV;
    @NotNull
    private final Boolean wantsPV;
    @NotNull
    private final Boolean deceased;
    @NotNull
    private final Boolean poster;
    @NotNull
    private final Boolean inaccessible;

    private final String telephone;
    private final String email;

    @JsonCreator
    public RecordContactRequest(@JsonProperty("intention") Integer intention,
                                @JsonProperty("likelihood") Integer likelihood,
                                @JsonProperty("cost") Boolean cost,
                                @JsonProperty("sovereignty") Boolean sovereignty,
                                @JsonProperty("border") Boolean border,
                                @JsonProperty("lift") Boolean lift,
                                @JsonProperty("hasVoted") Boolean hasVoted,
                                @JsonProperty("hasPV") Boolean hasPV,
                                @JsonProperty("wantsPV") Boolean wantsPV,
                                @JsonProperty("deceased") Boolean deceased,
                                @JsonProperty("poster") Boolean poster,
                                @JsonProperty("inaccessible") Boolean inaccessible,
                                @JsonProperty("telephone") String telephone,
                                @JsonProperty("email") String email) {
        this.intention = intention;
        this.likelihood = likelihood;
        this.cost = cost;
        this.sovereignty = sovereignty;
        this.border = border;
        this.lift = lift;
        this.hasVoted = hasVoted;
        this.hasPV = hasPV;
        this.wantsPV = wantsPV;
        this.deceased = deceased;
        this.poster = poster;
        this.inaccessible = inaccessible;
        this.email = email;
        this.telephone = nullToEmpty(telephone);
    }

    public Integer getIntention() {
        return intention;
    }

    public Integer getLikelihood() {
        return likelihood;
    }

    public Boolean getCost() {
        return cost;
    }

    public Boolean getSovereignty() {
        return sovereignty;
    }

    public Boolean getBorder() {
        return border;
    }

    public Boolean getLift() {
        return lift;
    }

    public Boolean getHasVoted() {
        return hasVoted;
    }

    public Boolean getHasPV() {
        return hasPV;
    }

    public Boolean getWantsPV() {
        return wantsPV;
    }

    public Boolean getDeceased() {
        return deceased;
    }

    public Boolean getPoster() {
        return poster;
    }

    public Boolean getInaccessible() {
        return inaccessible;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }
}
