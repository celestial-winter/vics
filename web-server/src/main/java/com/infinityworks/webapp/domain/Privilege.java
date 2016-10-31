package com.infinityworks.webapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "privileges")
public class Privilege extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private Permission permission;

    public Privilege(Permission permission) {
        this.permission = permission;
    }

    public Privilege() {
        // required by hibernate
    }

    public Permission getPermission() {
        return permission;
    }

    @JsonIgnore
    public boolean hasPermission(Permission permission) {
        return this.permission == permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Privilege)) return false;
        Privilege that = (Privilege) o;
        return Objects.equal(permission, that.permission);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(permission);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("permission", permission)
                .toString();
    }
}
