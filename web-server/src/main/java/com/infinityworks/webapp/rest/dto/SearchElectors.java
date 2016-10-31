package com.infinityworks.webapp.rest.dto;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.infinityworks.webapp.rest.validation.ValidSearchElectorsRequest;

import java.util.Map;

@ValidSearchElectorsRequest
public class SearchElectors {
    private final String surname;
    private final String postcode;
    private final String wardCode;

    public SearchElectors(String surname, String postcode, String wardCode) {
        this.surname =  surname;
        this.postcode = postcode;
        this.wardCode = wardCode;
    }

    public String getSurname() {
        return surname;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getWardCode() {
        return wardCode;
    }

    /**
     * Converts to upstream request format
     */
    public Map<String, String> getParameters() {
        return ImmutableMap.<String, String>builder()
                .put("postcode", postcode)
                .put("surname", surname)
                .put("ward", wardCode)
                .build();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("surname", surname)
                .add("postcode", postcode)
                .add("wardCode", wardCode)
                .toString();
    }
}
