package com.healthcore.appointmentservice.enums;

public enum UserRole {
    DOCTOR("DOCTOR"),
    NURSE("NURSE"),
    PATIENT("PATIENT"),
    ADMIN("ADMIN");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}