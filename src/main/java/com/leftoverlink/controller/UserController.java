package com.leftoverlink.controller;

import com.leftoverlink.model.User;
import com.leftoverlink.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(
        summary = "Upload profile image (secured)",
        description = "Upload profile image for the currently authenticated user."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Profile image uploaded successfully"
    )
    @PostMapping(value = "/profile/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RolesAllowed({"ROLE_DONOR", "ROLE_NGO"})  // Optional: restrict roles
    public ResponseEntity<String> uploadProfileImage(
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = optionalUser.get();
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/profiles");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        user.setProfileImageUrl("/uploads/profiles/" + fileName);
        userRepository.save(user);

        return ResponseEntity.ok("✅ Profile image uploaded successfully.");
    }

    @Operation(summary = "Get profile image URL (secured)")
    @GetMapping("/profile/image")
    @RolesAllowed({"ROLE_DONOR", "ROLE_NGO"})
    public ResponseEntity<String> getProfileImageUrl() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        String imageUrl = optionalUser.get().getProfileImageUrl();

        if (imageUrl == null || imageUrl.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No profile image found.");
        }

        String fullUrl = "http://localhost:8081" + imageUrl;
        return ResponseEntity.ok(fullUrl);
    }
}
