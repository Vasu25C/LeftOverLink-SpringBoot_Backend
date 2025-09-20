package com.leftoverlink.dto;

import com.leftoverlink.model.Role;

public class UserSearchResponse {
    private String name;
    private String email;
    private String phone;
    private Role role;

    public UserSearchResponse() {}

    public UserSearchResponse(String name, String email, String phone, Role role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
