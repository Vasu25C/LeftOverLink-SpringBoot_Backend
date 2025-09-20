package com.leftoverlink.controller;

import com.leftoverlink.dto.*;
import com.leftoverlink.model.FoodAcceptance;
import com.leftoverlink.model.User;
import com.leftoverlink.repository.FoodAcceptanceRepository;
import com.leftoverlink.service.AdminService;
import com.leftoverlink.service.BroadcastService;
import com.leftoverlink.service.UserService;
import org.springframework.data.domain.PageRequest;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final BroadcastService broadcastService;
    private final UserService userService;
    private final FoodAcceptanceRepository foodAcceptanceRepository;

    @Autowired
    public AdminController(AdminService adminService,
                           BroadcastService broadcastService,
                           UserService userService,
                           FoodAcceptanceRepository foodAcceptanceRepository) {
        this.adminService = adminService;
        this.broadcastService = broadcastService;
        this.userService = userService;
        this.foodAcceptanceRepository = foodAcceptanceRepository;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<UserSearchResponse>> searchUsers(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserSearchResponse> userPage = adminService.searchUsers(query, page, size);

        PagedResponse<UserSearchResponse> response = new PagedResponse<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/notify")
    public ResponseEntity<String> broadcast(@RequestBody BroadcastRequest request) {
        User currentAdmin = userService.getCurrentUser();
        broadcastService.broadcastToRole(request, currentAdmin.getEmail());
        return ResponseEntity.ok("Broadcast sent successfully.");
    }

    @GetMapping("/stats/food-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FoodStatusStatsResponse> getFoodStatusStats() {
        return ResponseEntity.ok(adminService.getFoodStatusStats());
    }

    @GetMapping("/reports/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> emailCsvReportToAdmin() {
        try {
            adminService.emailCsvReportToAdmin();
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    @GetMapping("/reports/users.csv")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportUsersCsv(HttpServletResponse response) throws IOException {
        adminService.exportUsersCsv(response);
    }

    @GetMapping("/reports/foods.csv")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportFoodsCsv(HttpServletResponse response) throws IOException {
        adminService.exportFoodsCsv(response);
    }

    @GetMapping("/foods")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<FoodSearchResponse>> searchFoodPosts(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FoodSearchResponse> foodPage = adminService.searchFoodPosts(query, page, size);

        PagedResponse<FoodSearchResponse> response = new PagedResponse<>(
                foodPage.getContent(),
                foodPage.getNumber(),
                foodPage.getSize(),
                foodPage.getTotalElements(),
                foodPage.getTotalPages(),
                foodPage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/acceptance-report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<AcceptanceReport>> getPaginatedAcceptances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FoodAcceptance> acceptancePage = foodAcceptanceRepository.findAll(PageRequest.of(page, size));

        List<AcceptanceReport> content = acceptancePage.getContent().stream()
        	    .map(fa -> new AcceptanceReport(
        	        fa.getId(),                         // ✅ Add this as the `id`
        	        fa.getFood().getId(),
        	        fa.getFood().getFoodName(),
        	        fa.getNgo().getName(),
        	        fa.getAcceptedQuantity(),
        	        fa.getStatus()
        	    ))
        	    .collect(Collectors.toList());


        PagedResponse<AcceptanceReport> response = new PagedResponse<>(
                content,
                acceptancePage.getNumber(),
                acceptancePage.getSize(),
                acceptancePage.getTotalElements(),
                acceptancePage.getTotalPages(),
                acceptancePage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminProfileResponse> getAdminProfile() {
        AdminProfileResponse response = new AdminProfileResponse(
            "Vasanth Kumar N P",
            "BE in Computer Science and Engineering",
            "FounderLeftOverLink@gmail.com",
            "6361400803",
            "/images/vasu.png", // Since the image is in static/images, this path will work
            "Hassan, Karnataka, India - 573131",
            String.join("\n",
                "LeftOverLink was founded with the vision of reducing food waste.",
                "Our mission is to connect surplus food with those in need.",
                "We believe in the power of technology for social good.",
                "Through partnerships, we ensure timely delivery of meals.",
                "As the founder, I am committed to creating a hunger-free future."
            )
        );

        return ResponseEntity.ok(response);
    }



}
