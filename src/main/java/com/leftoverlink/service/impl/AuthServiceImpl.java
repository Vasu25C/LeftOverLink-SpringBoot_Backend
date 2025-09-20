package com.leftoverlink.service.impl;

import com.leftoverlink.dto.*;
import com.leftoverlink.model.*;
import com.leftoverlink.repository.UserRepository;
import com.leftoverlink.security.JwtUtil;
import com.leftoverlink.service.AuthService;
import com.leftoverlink.service.EmailService;
import com.leftoverlink.service.GeoService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final GeoService geoService; // ✅ Add this
    private final EmailService emailService;
    
 
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           GeoService geoService , EmailService emailService) { // ✅ Include in constructor
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.geoService = geoService;
        this.emailService=emailService;
    }
    
    private final Map<String, String> otpMap = new HashMap<>();

    @Override
    public ResponseEntity<String> resetPassword(ResetPasswordRequest request) {
        String sentOtp = otpMap.get(request.getEmail());

        if (sentOtp == null || !sentOtp.equals(request.getOtp())) {
            return ResponseEntity.badRequest().body("❌ Invalid or expired OTP.");
        }

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpMap.remove(request.getEmail());  // Invalidate OTP
        return ResponseEntity.ok("✅ Password reset successful.");
    }
 
    
    @Override
    public ResponseEntity<String> sendPasswordResetOTP(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Email not registered.");
        }

        String otp = String.format("%04d", new Random().nextInt(10000));
        otpMap.put(email, otp);
        System.out.println("📧 Reset OTP for " + email + ": " + otp);

        // You may reuse your emailService to send this:
        String subject = "🔐 Password Reset OTP";
        String content = "Your password reset OTP is: <b>" + otp + "</b>";
        emailService.sendHtmlEmail(email, subject, content);

        return ResponseEntity.ok("✅ OTP sent to email.");
    }


    @Override
    public ResponseEntity<String> register(SignupRequest request) {
        if (request.getRole() == Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin registration not allowed.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        // ✅ Add address and pincode here
        user.setAddress(request.getAddress());
        user.setPincode(request.getPincode());
        String location = user.getAddress() + " " + user.getPincode();
        String[] coords = geoService.getCoordinates(location);
        if (coords != null) {
            user.setLatitude(coords[0]);
            user.setLongitude(coords[1]);
        }
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully.");
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        System.out.println("🔐 Login attempt for email: " + request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    System.out.println("❌ User not found for email: " + request.getEmail());
                    return new RuntimeException("Invalid credentials");
                });

        String rawPassword = request.getPassword();
        String storedHashedPassword = user.getPassword();

        System.out.println("➡️ Raw password entered: " + rawPassword);
        System.out.println("🔒 Stored hashed password: " + storedHashedPassword);

        boolean isMatch = passwordEncoder.matches(rawPassword, storedHashedPassword);
        System.out.println("✅ Password match result: " + isMatch);

        if (!isMatch) {
            System.out.println("❌ Password did not match for user: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        System.out.println("🎟️ JWT generated for user: " + request.getEmail());

        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name(), "Login successful"));
    }

}
