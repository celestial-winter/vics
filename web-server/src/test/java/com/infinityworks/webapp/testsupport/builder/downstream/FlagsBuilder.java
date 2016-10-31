package com.infinityworks.webapp.testsupport.builder.downstream;

import com.infinityworks.webapp.rest.dto.Flags;

public class FlagsBuilder {
    private Integer intentionFrom;
    private Integer intentionTo;
    private Integer likelihoodFrom;
    private Integer likelihoodTo;
    private Boolean hasPV;
    private Boolean wantsPv;
    private Boolean needsLift;
    private Boolean notCanvassedYet;
    private Boolean poster;
    private Boolean email;
    private Boolean telephone;

    public static FlagsBuilder flags() {
        return new FlagsBuilder().withDefaults();
    }

    public FlagsBuilder withDefaults() {
        intentionFrom = 1;
        intentionTo = 5;
        likelihoodFrom = 1;
        likelihoodTo = 5;
        hasPV = false;
        wantsPv = false;
        needsLift = false;
        notCanvassedYet = false;
        poster = false;
        email = false;
        telephone = false;
        return this;
    }

    public FlagsBuilder withIntentionFrom(Integer intentionFrom) {
        this.intentionFrom = intentionFrom;
        return this;
    }

    public FlagsBuilder withIntentionTo(Integer intentionTo) {
        this.intentionTo = intentionTo;
        return this;
    }

    public FlagsBuilder withLikelihoodFrom(Integer likelihoodFrom) {
        this.likelihoodFrom = likelihoodFrom;
        return this;
    }

    public FlagsBuilder withLikelihoodTo(Integer likelihoodTo) {
        this.likelihoodTo = likelihoodTo;
        return this;
    }

    public FlagsBuilder withHasPV(Boolean hasPV) {
        this.hasPV = hasPV;
        return this;
    }

    public FlagsBuilder withWantsPv(Boolean wantsPv) {
        this.wantsPv = wantsPv;
        return this;
    }

    public FlagsBuilder withNeedsLift(Boolean needsLift) {
        this.needsLift = needsLift;
        return this;
    }

    public FlagsBuilder withNotCanvassedYet(Boolean notCanvassedYet) {
        this.notCanvassedYet = notCanvassedYet;
        return this;
    }

    public FlagsBuilder withPoster(Boolean poster) {
        this.poster = poster;
        return this;
    }

    public FlagsBuilder email(Boolean email) {
        this.email = email;
        return this;
    }

    public FlagsBuilder withTelephone(Boolean telephone) {
        this.telephone = telephone;
        return this;
    }

    public Flags build() {
        return new Flags(intentionFrom, intentionTo, likelihoodFrom, likelihoodTo, hasPV, wantsPv, needsLift, notCanvassedYet, poster, email, telephone);
    }
}