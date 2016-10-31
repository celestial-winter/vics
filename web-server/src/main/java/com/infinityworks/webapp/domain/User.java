package com.infinityworks.webapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.error.NotAuthorizedFailure;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Main user domain model. From a security aspect, there are 3 orthogonal concepts bound to
 * a user:
 * 1) Role (Admin, User) - which determines whether a user has elevated privileges or not,
 * e.g. if a user is ROLE_ADMIN then they will get all privileges
 * 2) Privileges - determines what a user can DO, e.g. read/write access
 * 3) Application restrictions - determines what type of data the user can access, e.g.
 * which wards, which constituencies
 * <p>
 * Note: modifying this class will invalidate all current user sessions,
 * since spring session serializes this entity by default.
 * To change this behaviour we would need to override the spring session methods
 * that persist the session to Redis
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    public static final String SUPER_NAME = "admin@admin.me";

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Boolean writeAccess;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Convert(converter = PersistentLocalDateTime.class)
    private LocalDateTime lastLogin;

    @Convert(converter = PersistentLocalDateTime.class)
    private LocalDateTime created;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_privileges",
            joinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privileges_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"users_id", "privileges_id"})
    )
    private Set<Privilege> permissions;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_constituencies",
            joinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "constituencies_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"users_id", "constituencies_id"}))
    private Set<Constituency> constituencies;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_wards",
            joinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "wards_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"users_id", "wards_id"}))
    private Set<Ward> wards;

    @JsonIgnore
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Privilege> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Privilege> permissions) {
        this.permissions = permissions;
    }

    public Set<Constituency> getConstituencies() {
        return constituencies;
    }

    public void removeConstituency(Constituency constituency) {
        constituencies.remove(constituency);
    }

    public void setConstituencies(Set<Constituency> constituencies) {
        this.constituencies = constituencies;
    }

    public Set<Ward> getWards() {
        return wards;
    }

    public void removeWard(Ward ward) {
        wards.remove(ward);
    }

    public void setWards(Set<Ward> wards) {
        this.wards = wards;
    }

    public Boolean getWriteAccess() {
        return writeAccess;
    }

    public void setWriteAccess(Boolean writeAccess) {
        this.writeAccess = writeAccess;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    /**
     * Gets the wards available to a user. This includes the directly allocated wards
     * and the wards contained within a constituency.
     *
     * @return unique set of wards a user has access to
     */
    @JsonIgnore
    public Set<Ward> getAccessibleWards() {
        Set<Ward> wards = getConstituencies().stream()
                .map(Constituency::getWards)
                .flatMap(Collection::stream)
                .collect(toSet());
        wards.addAll(getWards());
        return wards;
    }

    public boolean hasWardPermission(Ward ward) {
        return getWards().contains(ward) ||
                getConstituencies().contains(ward.getConstituency());
    }

    public boolean hasConstituencyPermission(Constituency constituency) {
        return getConstituencies().contains(constituency);
    }

    public boolean hasPermission(Permission permission) {
        return getPermissions().stream().anyMatch(privilege -> privilege.getPermission() == permission);
    }

    public Boolean hasWriteAccess() {
        return getWriteAccess();
    }

    public Try<User> ensureWriteAccess() {
        if (getWriteAccess()) {
            return Try.success(this);
        } else {
            return Try.failure(new NotAuthorizedFailure("User does not have write access"));
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equal(username, user.username) &&
                Objects.equal(firstName, user.firstName) &&
                Objects.equal(lastName, user.lastName) &&
                Objects.equal(writeAccess, user.writeAccess) &&
                role == user.role;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(username, firstName, lastName, writeAccess, role);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("username", username)
                .add("role", role)
                .toString();
    }
}
