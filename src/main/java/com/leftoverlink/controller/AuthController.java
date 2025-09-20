package com.leftoverlink.controller;

import com.leftoverlink.dto.*;
import com.leftoverlink.model.User;
import com.leftoverlink.repository.FoodAcceptanceRepository;
import com.leftoverlink.repository.UserRepository;
import com.leftoverlink.service.AuthService;
import com.leftoverlink.service.GeoService;
import com.leftoverlink.service.OTPService;

import jakarta.mail.MessagingException;

import com.leftoverlink.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private GeoService geoService;

    @Autowired
    private FoodAcceptanceRepository foodAcceptanceRepository;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    public AuthController(AuthService authService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        String otp = otpService.generateAndSendOtp(email);
        return ResponseEntity.ok("OTP sent to " + email);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> sendForgotPasswordOTP(@RequestParam String email) {
        return authService.sendPasswordResetOTP(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }

    

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        if (otpService.validateOtp(email, otp)) {
            return ResponseEntity.ok("OTP verified. Please resend register request to complete signup.");
        } else {
            return ResponseEntity.status(400).body("Invalid OTP.");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody SignupRequest request) {
        System.out.println("📥 Register request received");

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        if (!otpService.isOtpValidated(request.getEmail())) {
            otpService.generateAndSendOtp(request.getEmail());
            return ResponseEntity.status(202).body("OTP sent to email. Please verify before registering.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setAddress(request.getAddress());
        user.setPincode(request.getPincode());

       /* String location = request.getAddress() + ", " + request.getPincode();
        String[] coords = geoService.getCoordinates(location);

        if (coords != null) {
            user.setLatitude(coords[0]);
            user.setLongitude(coords[1]);
        }*/
        String location = request.getAddress() + ", " + request.getPincode();
        String[] coords = geoService.getCoordinates(location);

        if (coords != null && coords.length == 2 && coords[0] != null && coords[1] != null) {
            user.setLatitude(coords[0]);
            user.setLongitude(coords[1]);
            System.out.println("🌍 Geolocation fetched: lat=" + coords[0] + ", lon=" + coords[1]);
        } else {
            System.out.println("❌ Coordinates not found for: " + location);
        }


        user.setCreditPoints(0);
        userRepository.save(user);
        System.out.println("💾 User saved to database");

        try {
            emailService.sendWelcomeEmailWithInstructions(user);
        } catch (MessagingException e) {
            e.printStackTrace(); // or log.error(...)
            return ResponseEntity.status(500).body("User created, but failed to send welcome email.");
        }

        return ResponseEntity.ok("User registered successfully.");
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/profile")
    public UserProfileResponse getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        String location = user.getAddress() + ", " + user.getPincode();

        return new UserProfileResponse(
                user.getName(),
                user.getRole().name(),
                user.getCreditPoints(),
                user.getEmail(),
                user.getProfileImageUrl(),
                location
        );
    }

    @PostMapping("/profile/upload-image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/profiles");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        user.setProfileImageUrl("/uploads/profiles/" + fileName);
        userRepository.save(user);

        return ResponseEntity.ok("Profile image uploaded successfully.");
    }
}
