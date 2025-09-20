package com.leftoverlink.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.leftoverlink.dto.*;
import com.leftoverlink.model.*;
import com.leftoverlink.repository.*;
import com.leftoverlink.security.JwtUtil;
import com.leftoverlink.service.*;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/food")
public class FoodController {
	private final JwtUtil jwtUtil;
    private final FoodService foodService;
    private final FoodRepository foodRepository;
    private final CertificateService certificateService;
    private final UserRepository userRepository;
    private final DistanceService distanceService;
    private final EmailService emailService;
    @Autowired
    private FoodAcceptanceRepository foodAcceptanceRepository;
    public FoodController(FoodService foodService,
                          FoodRepository foodRepository,
                          CertificateService certificateService,
                          UserRepository userRepository,
                          EmailService emailService,
                          DistanceService distanceService , JwtUtil jwtUtil) {
        this.foodService = foodService;
        this.foodRepository = foodRepository;
        this.certificateService = certificateService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.distanceService = distanceService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/post-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postFoodWithImage(
            @RequestParam("email") String email,
            @RequestParam("foodName") String foodName,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("expiryDate") String expiryDate,
            @RequestPart("image") MultipartFile image) {

        try {
            if (email.isBlank() || foodName.isBlank() || expiryDate.isBlank() || quantity == null || quantity <= 0 || image.isEmpty()) {
                return ResponseEntity.badRequest().body("All fields are required and must be valid.");
            }

            User donor = userRepository.findByEmail(email.trim())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!"DONOR".equalsIgnoreCase(donor.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only donors can post food.");
            }

            String safeFileName = foodName.trim().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase() + ".jpg";
            Path savePath = Paths.get("src/main/resources/static/images/");
            Files.createDirectories(savePath);
            Path finalPath = savePath.resolve(safeFileName);

            if (Files.exists(finalPath)) {
                return ResponseEntity.badRequest().body("Image with this food name already exists. Please choose a different name.");
            }

            Files.copy(image.getInputStream(), finalPath);

            try (OutputStream out = new FileOutputStream(finalPath.toFile(), true)) {
                out.flush();
            }

            Thread.sleep(200);

            if (!Files.exists(finalPath)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image could not be verified after saving.");
            }

            String dbImageUrl = "/images/" + safeFileName;

            Food food = new Food();
            food.setDonor(donor);
            food.setFoodName(foodName.trim());
            food.setQuantity(quantity);
            food.setRemainingQuantity(quantity);
            food.setExpiryDate(LocalDate.parse(expiryDate.trim()));
            food.setImageUrl(dbImageUrl);
            food.setStatus("POSTED");
            food.setLatitude(donor.getLatitude());
            food.setLongitude(donor.getLongitude());

            foodRepository.save(food);

            List<User> ngos = userRepository.findAll().stream()
                    .filter(u -> "NGO".equalsIgnoreCase(u.getRole().name()) && u.getLatitude() != null && u.getLongitude() != null)
                    .toList();

            for (User ngo : ngos) {
                double distanceKm = distanceService.calculateDistance(
                        donor.getLatitude(), donor.getLongitude(),
                        ngo.getLatitude(), ngo.getLongitude()) / 1000.0;

                if (distanceKm <= 60.0) {
                    emailService.sendNearbyDonationAlert(ngo, food, distanceKm);
                }
            }

            return ResponseEntity.ok("✅ Food posted successfully and alerts sent.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error posting food: " + e.getMessage());
        }
    }
/*
    @PostMapping("/post")
    public ResponseEntity<String> postFoodJson(@RequestBody FoodPostJsonRequest request) {
        try {
            return foodService.postFoodJson(request);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to post food: " + e.getMessage());
        }
    }
*/
    @PostMapping("/post")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<String> postFoodJson(@RequestBody FoodPostJsonRequest request,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return foodService.postFoodJson(request, userDetails.getUsername());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to post food: " + e.getMessage());
        }
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('NGO')")
    public List<FoodSearchResponse> getAvailableFood(@RequestHeader("Authorization") String token,
                                                     @RequestParam(required = false) Double maxDistance) {
        String email = jwtUtil.extractUsername(token.substring(7)); // remove 'Bearer '
        return foodService.getAvailableFood(email, maxDistance);
    }


    @PostMapping("/accept/{foodId}")
    public ResponseEntity<String> acceptFood(@PathVariable Long foodId,
                                             @RequestBody Map<String, Integer> body,
                                             Authentication authentication) {
        if (!body.containsKey("quantity")) {
            return ResponseEntity.badRequest().body("Missing quantity");
        }

        int requestedQuantity = body.get("quantity");
        String ngoEmail = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            ngoEmail = userDetails.getUsername(); // from JWT
        } else if (authentication != null && authentication.getPrincipal() instanceof String emailStr) {
            ngoEmail = emailStr;
        } else {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return foodService.acceptFood(foodId, ngoEmail, requestedQuantity);
    }



    @PutMapping("/mark-complete/{foodId}")
    public ResponseEntity<String> markComplete(@PathVariable Long foodId,
                                         @RequestHeader("email") String ngoEmail) {
        return foodService.markDonationComplete(foodId, ngoEmail);
    }

    @GetMapping("/my-donations")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<List<FoodResponse>> getMyDonations(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(foodService.getMyDonations(email));
    }


    @PreAuthorize("hasRole('NGO')")
    @GetMapping("/my-accepted-donations")
    public ResponseEntity<List<NGODonationResponse>> getMyAcceptedDonations(Authentication authentication) {
        String email = authentication.getName(); // Extract from JWT
        return ResponseEntity.ok(foodService.getMyAcceptedDonations(email));
    }


    @GetMapping("/profile/certificate/donor")
    public ResponseEntity<InputStreamResource> downloadDonorCertificate(@RequestHeader("email") String email) throws IOException {
        User donor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        Food latestDonation = foodRepository.findTopByDonorOrderByIdDesc(donor)
                .orElseThrow(() -> new RuntimeException("No donations found"));

        return certificateService.generateDonorCertificate(donor, latestDonation);
    }

    @GetMapping("/profile/certificate/ngo")
    public ResponseEntity<InputStreamResource> downloadNGOCertificate(@RequestHeader("email") String email) throws IOException {
        User ngo = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        FoodAcceptance latest = foodAcceptanceRepository.findTopByNgoOrderByIdDesc(ngo)
                .orElseThrow(() -> new RuntimeException("No accepted donations"));

        return certificateService.generateNGOCertificate(ngo, latest.getFood(), latest.getAcceptedQuantity());
    }
}
