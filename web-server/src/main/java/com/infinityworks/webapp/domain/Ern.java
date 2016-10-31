package com.infinityworks.webapp.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.infinityworks.webapp.converter.ErnSerializer;

@JsonSerialize(using = ErnSerializer.class)
public class Ern {
    public static final String REGEX = "^\\w{1,10}-\\w{1,8}-\\d{1,8}-\\w{1,3}$";

    private final String wardCode;
    private final String pollingDistrict;
    private final String number;
    private final String suffix;

    public Ern(String wardCode, String pollingDistrict, String number, String suffix) {
        this.wardCode = wardCode;
        this.pollingDistrict = pollingDistrict;
        this.number = number;
        this.suffix = suffix;
    }

    public static Ern valueOf(String value) {
        if (!value.matches(REGEX)) {
            throw new IllegalArgumentException("Invalid ERN format");
        } else {
            String[] parts = value.split("-");
            return new Ern(parts[0], parts[1], parts[2], parts[3]);
        }
    }

    public String getPollingDistrict() {
        return pollingDistrict;
    }

    public String getWardCode() {
        return wardCode;
    }

    public String getNumber() {
        return number;
    }

    public String getSuffix() {
        return suffix;
    }

    public String longForm() {
        return getWardCode() + "-" + getPollingDistrict() + "-" + getNumber() + "-" + getSuffix();
    }

    public String shortForm() {
        return getPollingDistrict() + "-" + getNumber() + "-" + getSuffix();
    }

    @Override
    public String toString() {
        return longForm();
    }
}
