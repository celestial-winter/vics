package com.infinityworks.webapp.clients.pdfserver.dto;

public class RequestInfo {
    private final String wardCode;
    private final String wardName;
    private final String constituencyName;

    public RequestInfo(String wardCode, String wardName, String constituencyName) {
        this.wardCode = wardCode;
        this.wardName = wardName;
        this.constituencyName = constituencyName;
    }

    public String getWardCode() {
        return wardCode;
    }

    public String getWardName() {
        return wardName;
    }

    public String getConstituencyName() {
        return constituencyName;
    }
}
