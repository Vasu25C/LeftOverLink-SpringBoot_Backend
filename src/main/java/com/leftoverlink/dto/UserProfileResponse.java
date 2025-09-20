package com.leftoverlink.dto;

public class UserProfileResponse {
    private String name;
    private String role;
    private int creditPoints;
    private String email;
    private String profileImageUrl;
    private String location;

    public UserProfileResponse(String name, String role, int creditPoints, String email, String profileImageUrl, String location) {
        this.name = name;
        this.role = role;
        this.creditPoints = creditPoints;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.location = location;
    }

    // Getters
    public String getName() { return name; }
    public String getRole() { return role; }
    public int getCreditPoints() { return creditPoints; }
    public String getEmail() { return email; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getLocation() { return location; }
}
