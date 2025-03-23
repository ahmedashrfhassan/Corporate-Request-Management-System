package com.warba.assessment.exception;

public enum Messages {
    USER_NOT_FOUND("User not found with ID: %s"),
    EXPIRED_CIVIL_ID("Civil ID is expired"),
    REQUEST_NOT_FOUND("Request not found with ID: %d"),
    STATUS_NOT_FOUND("Status not found with ID: %D");

    private String value;

    Messages(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public String evaluated(Object... parameters) {
        return value.formatted(parameters);
    }
}
