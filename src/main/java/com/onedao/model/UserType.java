package com.onedao.model;

public enum UserType {
    STANDARD_USER("Standard User"),
    ADMINISTRATOR("Administrator");

    private final String label;

    UserType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // Converts "Standard User" / "Administrator" text into the enum
    public static UserType fromLabel(String text) {
        for (UserType type : values()) {
            if (type.label.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid user type: " + text);
    }
}