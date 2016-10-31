package com.infinityworks.webapp.testsupport.builder.downstream;

import com.infinityworks.webapp.rest.dto.RecordContactRequest;

public class RecordContactRequestBuilder {
    private Integer intention;
    private Integer likelihood;
    private Boolean cost;
    private Boolean sovereignty;
    private Boolean border;
    private Boolean lift;
    private Boolean hasVoted;
    private Boolean hasPV;
    private Boolean wantsPV;
    private Boolean deceased;
    private Boolean poster;
    private Boolean inaccessible;
    private String telephone;
    private String email;

    public static RecordContactRequestBuilder recordContactRequest() {
        return new RecordContactRequestBuilder().withDefaults();
    }

    public RecordContactRequestBuilder withDefaults() {
        withIntention(3)
                .withLikelihood(3)
                .withCost(false)
                .withSovereignty(false)
                .withBorder(true)
                .withLift(false)
                .withHasVoted(true)
                .withHasPV(true)
                .withWantsPV(false)
                .withDeceased(false)
                .withPoster(false)
                .withInaccessible(false)
                .withTelephone("07980123456")
                .withEmail("a@b.com");
        return this;
    }

    public RecordContactRequestBuilder withIntention(Integer intention) {
        this.intention = intention;
        return this;
    }

    public RecordContactRequestBuilder withLikelihood(Integer likelihood) {
        this.likelihood = likelihood;
        return this;
    }

    public RecordContactRequestBuilder withCost(Boolean cost) {
        this.cost = cost;
        return this;
    }

    public RecordContactRequestBuilder withSovereignty(Boolean sovereignty) {
        this.sovereignty = sovereignty;
        return this;
    }

    public RecordContactRequestBuilder withBorder(Boolean border) {
        this.border = border;
        return this;
    }

    public RecordContactRequestBuilder withLift(Boolean lift) {
        this.lift = lift;
        return this;
    }

    public RecordContactRequestBuilder withHasVoted(Boolean hasVoted) {
        this.hasVoted = hasVoted;
        return this;
    }

    public RecordContactRequestBuilder withHasPV(Boolean hasPV) {
        this.hasPV = hasPV;
        return this;
    }

    public RecordContactRequestBuilder withWantsPV(Boolean wantsPV) {
        this.wantsPV = wantsPV;
        return this;
    }

    public RecordContactRequestBuilder withDeceased(Boolean deceased) {
        this.deceased = deceased;
        return this;
    }

    public RecordContactRequestBuilder withPoster(Boolean poster) {
        this.poster = poster;
        return this;
    }

    public RecordContactRequestBuilder withInaccessible(Boolean inaccessible) {
        this.inaccessible = inaccessible;
        return this;
    }

    public RecordContactRequestBuilder withTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public RecordContactRequestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public RecordContactRequest build() {
        return new RecordContactRequest(intention, likelihood, cost, sovereignty, border, lift, hasVoted, hasPV, wantsPV, deceased, poster, inaccessible, telephone, email);
    }
}