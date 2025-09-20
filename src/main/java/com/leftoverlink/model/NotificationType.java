package com.leftoverlink.model;

public enum NotificationType {
    DONATION_ALERT,       // Email sent to NGOs nearby
    CERTIFICATE_READY,    // After successful completion
    CREDIT_EARNED,        // After donor/NGO gains a point
    TOP_DONOR,            // Admin pushes top donor highlight
    TOP_NGO,              // Admin pushes top NGO highlight
    FOOD_ACCEPTED,        // NGO accepts a donation
    WELCOME,              // On successful signup
    GENERAL,              // Any other general info
    DONATION_POSTED       // Donor posts a new donation
}
