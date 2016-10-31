package com.infinityworks.webapp.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.UUID;

public class CurrentUser extends org.springframework.security.core.userdetails.User {
    private User user;

    public CurrentUser(User user) {
        super(user.getUsername(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public UUID getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrentUser)) return false;
        if (!super.equals(o)) return false;
        CurrentUser that = (CurrentUser) o;
        return Objects.equal(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), user);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("user", user)
                .toString();
    }
}