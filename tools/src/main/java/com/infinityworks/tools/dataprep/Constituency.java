package com.infinityworks.tools.dataprep;

import com.google.common.base.Objects;

import java.util.UUID;

public class Constituency {
    private UUID id;
    private String code;
    private String name;

    public Constituency(String code, String name) {
        id = UUID.randomUUID();
        this.code = code;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constituency)) return false;
        Constituency that = (Constituency) o;
        return Objects.equal(code, that.code) &&
                Objects.equal(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code, name);
    }
}

