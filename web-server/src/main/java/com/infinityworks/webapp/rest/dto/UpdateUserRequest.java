package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserRequest {
    @NotEmpty
    private final String firstName;
    @NotEmpty
    private final String lastName;
    @Email
    private final String username;
    private final String password;
    private final String repeatPassword;
    @NotNull
    private final boolean writeAccess;

    @JsonCreator
    public UpdateUserRequest(@JsonProperty("firstName") String firstName,
                             @JsonProperty("lastName") String lastName,
                             @JsonProperty("username") String username,
                             @JsonProperty("password") String password,
                             @JsonProperty("repeatPassword") String repeatPassword,
                             @JsonProperty("writeAccess") boolean writeAccess) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.writeAccess = writeAccess;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public boolean getWriteAccess() {
        return writeAccess;
    }
}
