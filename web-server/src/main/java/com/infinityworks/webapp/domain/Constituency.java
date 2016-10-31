package com.infinityworks.webapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "constituencies")
public class Constituency extends BaseEntity {
    private String code;
    private String name;

    @OneToMany(mappedBy = "constituency", fetch = FetchType.EAGER)
    private Set<Ward> wards;

    public Constituency() {
        // required by hibernate
    }

    public Constituency(String code, String name) {
        this.code = code;
        this.name = name;
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

    @JsonIgnore
    public Set<Ward> getWards() {
        return wards;
    }

    public void setWards(Set<Ward> wards) {
        this.wards = wards;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("name", name)
                .toString();
    }
}
