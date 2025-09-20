package com.leftoverlink.dto;

public class BroadcastRequest {
    private String subject;
    private String message;
    private String role; // ALL, DONOR, NGO
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

    // Getters & Setters...
}
