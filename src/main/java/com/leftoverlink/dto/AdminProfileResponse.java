package com.leftoverlink.dto;

public class AdminProfileResponse {
    private String name;
    private String qualification;
    private String email;
    private String contact;
    private String profileImageUrl;
    private String location;
    private String aboutFounder;

    public AdminProfileResponse(String name, String qualification, String email, String contact, String profileImageUrl, String location, String aboutFounder) {
        this.name = name;
        this.qualification = qualification;
        this.email = email;
        this.contact = contact;
        this.profileImageUrl = profileImageUrl;
        this.location = location;
        this.aboutFounder = aboutFounder;
    }

    // Getters
    public String getName() { return name; }
    public String getQualification() { return qualification; }
    public String getEmail() { return email; }
    public String getContact() { return contact; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getLocation() { return location; }
    public String getAboutFounder() { return aboutFounder; }
}
