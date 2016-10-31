package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.infinityworks.webapp.domain.Role;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserRequest {

    @NotEmpty
    @Email
    private final String username;
    @NotEmpty
    private final String password;
    @NotEmpty
    private final String firstName;
    private final String lastName;
    @NotEmpty
    private final String repeatPassword;
    @NotNull
    private final Role role;
    @NotNull
    private final boolean writeAccess;

    @JsonCreator
    public CreateUserRequest(@JsonProperty("username") String username,
                             @JsonProperty("password") String password,
                             @JsonProperty("firstName") String firstName,
                             @JsonProperty("lastName") String lastName,
                             @JsonProperty("repeatPassword") String repeatPassword,
                             @JsonProperty("role") Role role,
                             @JsonProperty("writeAccess") boolean writeAccess) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.repeatPassword = repeatPassword;
        this.role = role;
        this.writeAccess = writeAccess;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public Role getRole() {
        return role;
    }

    public boolean getWriteAccess() {
        return writeAccess;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
