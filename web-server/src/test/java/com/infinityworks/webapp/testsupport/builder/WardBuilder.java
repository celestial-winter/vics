package com.infinityworks.webapp.testsupport.builder;

import com.infinityworks.webapp.domain.Constituency;
import com.infinityworks.webapp.domain.Ward;

public class WardBuilder {
    private String wardName;
    private String wardCode;
    private Constituency constituency;

    public static WardBuilder ward() {
        return new WardBuilder().withDefaults();
    }

    public WardBuilder withDefaults() {
        Constituency c = new ConstituencyBuilder().build();
        c.setCode("E14000651");
        c.setName("Coventry South");

        withWardName("Earlsdon")
                .withWardCode("E05001221")
                .withConstituency(c);
        return this;
    }

    public WardBuilder withWardName(String val) {
        wardName = val;
        return this;
    }

    public WardBuilder withWardCode(String val) {
        wardCode = val;
        return this;
    }

    public WardBuilder withConstituency(Constituency val) {
        constituency = val;
        return this;
    }

    public Ward build() {
        Ward ward = new Ward();
        ward.setName(wardName);
        ward.setCode(wardCode);
        ward.setConstituency(constituency);
        return ward;
    }

    private WardBuilder() {
    }
}
