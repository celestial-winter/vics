package com.infinityworks.tools.dataprep;

import com.google.common.base.Objects;

import java.util.UUID;

public class Ward {
    private UUID id;
    private String wardName;
    private String wardCode;
    private UUID constituency;

    public Ward(String wardName, String wardCode, UUID constituency) {
        id = UUID.randomUUID();
        this.wardName = wardName;
        this.wardCode = wardCode;
        this.constituency = constituency;
    }

    public UUID getId() {
        return id;
    }

    public String getWardName() {
        return wardName;
    }

    public String getWardCode() {
        return wardCode;
    }

    public UUID getConstituency() {
        return constituency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ward)) return false;
        Ward ward = (Ward) o;
        return Objects.equal(wardName, ward.wardName) &&
                Objects.equal(wardCode, ward.wardCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(wardName, wardCode);
    }
}
