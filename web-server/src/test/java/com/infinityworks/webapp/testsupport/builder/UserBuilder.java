package com.infinityworks.webapp.testsupport.builder;

import com.infinityworks.webapp.domain.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class UserBuilder {
    private String username;
    private String firstName;
    private String lastName;
    private String passwordHash;
    private Boolean writeAccess;
    private Role role;
    private Set<Privilege> permissions;
    private Set<Constituency> constituencies;
    private Set<Ward> wards;

    public static UserBuilder user() {
        return new UserBuilder().withDefaults();
    }

    public UserBuilder withDefaults() {
        return withFirstName("Jane")
                .withLastName("Doe")
                .withWards(newHashSet())
                .withConstituencies(newHashSet())
                .withPasswordHash(BCrypt.hashpw("password", BCrypt.gensalt()))
                .withRole(Role.USER)
                .withUsername("jane@doe.com");
    }

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder withPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public UserBuilder withWriteAccess(Boolean writeAccess) {
        this.writeAccess = writeAccess;
        return this;
    }

    public UserBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public UserBuilder withPermissions(Set<Privilege> permissions) {
        this.permissions = permissions;
        return this;
    }

    public UserBuilder withConstituencies(Set<Constituency> constituencies) {
        this.constituencies = constituencies;
        return this;
    }

    public UserBuilder withWards(Set<Ward> wards) {
        this.wards = wards;
        return this;
    }

    public User build() {
        User user = new User();
        user.initNew();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordHash(passwordHash);
        user.setWriteAccess(writeAccess);
        user.setRole(role);
        user.setPermissions(permissions);
        user.setConstituencies(constituencies);
        user.setWards(wards);
        return user;
    }
}