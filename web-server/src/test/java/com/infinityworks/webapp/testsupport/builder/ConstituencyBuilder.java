package com.infinityworks.webapp.testsupport.builder;

import com.infinityworks.webapp.domain.Constituency;

public class ConstituencyBuilder {
    private String code;
    private String name;

    public static ConstituencyBuilder constituency() {
        return new ConstituencyBuilder().withDefaults();
    }

    public ConstituencyBuilder withDefaults() {
        withCode("E14000651").withName("Coventry South");
        return this;
    }

    public ConstituencyBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public ConstituencyBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public Constituency build() {
        Constituency constituency = new Constituency(code, name);
        constituency.initNew();
        return constituency;
    }
}