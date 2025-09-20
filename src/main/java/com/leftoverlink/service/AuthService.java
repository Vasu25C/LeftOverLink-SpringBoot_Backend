package com.leftoverlink.service;

import com.leftoverlink.dto.LoginRequest;
import com.leftoverlink.dto.SignupRequest;
import com.leftoverlink.dto.AuthResponse;
import com.leftoverlink.dto.ResetPasswordRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<String> register(SignupRequest request);
    ResponseEntity<AuthResponse> login(LoginRequest request);

    // ✅ Add these two
    ResponseEntity<String> sendPasswordResetOTP(String email);
    ResponseEntity<String> resetPassword(ResetPasswordRequest request);
}
