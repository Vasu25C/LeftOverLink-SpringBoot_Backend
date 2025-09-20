package com.leftoverlink.dto;

import com.leftoverlink.model.User;

public class UserSummary {
    private String name;
    private String email;
    private int creditPoints; // primitive int is okay if we handle null safely

    public UserSummary(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.creditPoints = user.getCreditPoints() != null ? user.getCreditPoints() : 0; // ✅ null-safe
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getCreditPoints() {
        return creditPoints;
    }
}
