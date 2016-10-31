package com.infinityworks.webapp.domain;

public enum Role {
    USER, ADMIN;

    public static boolean hasRole(Role underTest, Role requiredUser) {
        return !(requiredUser == Role.ADMIN && underTest == Role.USER);
    }
}
