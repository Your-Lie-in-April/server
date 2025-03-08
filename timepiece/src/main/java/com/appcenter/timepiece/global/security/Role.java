package com.appcenter.timepiece.global.security;

public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_TEST("ROLE_TEST");

    String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
