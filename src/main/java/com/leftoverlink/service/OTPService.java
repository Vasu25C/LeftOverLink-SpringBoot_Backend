package com.leftoverlink.service;

public interface OTPService {
    String generateAndSendOtp(String email);
    boolean validateOtp(String email, String otp);
    boolean isOtpValidated(String email); // ✅ New method
    void markOtpValidated(String email);  // ✅ New method
}
