package com.infinityworks.webapp.testsupport.builder.downstream;

import com.infinityworks.webapp.rest.dto.StreetRequest;

public class StreetBuilder {
    private String mainStreet;
    private String postTown;
    private String dependentStreet;
    private String dependentLocality;

    public static StreetBuilder street() {
        return new StreetBuilder().withDefaults();
    }

    public StreetBuilder withDefaults() {
        withMainStreet("Highfield Road")
                .withDependentLocality("")
                .withDependentStreet("")
                .withPostTown("Coventry");
        return this;
    }

    public StreetBuilder withMainStreet(String mainStreet) {
        this.mainStreet = mainStreet;
        return this;
    }

    public StreetBuilder withPostTown(String postTown) {
        this.postTown = postTown;
        return this;
    }

    public StreetBuilder withDependentStreet(String dependentStreet) {
        this.dependentStreet = dependentStreet;
        return this;
    }

    public StreetBuilder withDependentLocality(String dependentLocality) {
        this.dependentLocality = dependentLocality;
        return this;
    }

    public StreetRequest build() {
        return new StreetRequest(mainStreet, postTown, dependentStreet, dependentLocality);
    }
}