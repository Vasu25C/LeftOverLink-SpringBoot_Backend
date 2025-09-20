package com.leftoverlink.service.impl;

import com.leftoverlink.dto.*;
import com.leftoverlink.model.*;
import com.leftoverlink.repository.*;
import com.leftoverlink.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.leftoverlink.model.*;
import com.leftoverlink.repository.*;
import com.leftoverlink.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {


    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final EmailService emailService;
    private final GeoService geoService;
    private final DistanceService distanceService;
    private final CertificateService certificateService;
    private final FoodAcceptanceRepository foodAcceptanceRepository;

    private final NotificationService notificationService;

    public FoodServiceImpl(
            UserRepository userRepository,
            FoodRepository foodRepository,
            EmailService emailService,
            GeoService geoService,
            DistanceService distanceService,
            CertificateService certificateService,
            FoodAcceptanceRepository foodAcceptanceRepository,
            NotificationService notificationService // ← ADD THIS
    ) {
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.emailService = emailService;
        this.geoService = geoService;
        this.distanceService = distanceService;
        this.certificateService = certificateService;
        this.foodAcceptanceRepository = foodAcceptanceRepository;
        this.notificationService = notificationService; // ← SET THIS
    }
 
    private static final Logger log = LoggerFactory.getLogger(FoodServiceImpl.class);

    @Override
    public ResponseEntity<String> postFoodJson(FoodPostJsonRequest request) throws IOException {
        // Default fallback if email not passed directly — extract from SecurityContext
        String email = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> auth.getName())
                .orElseThrow(() -> new RuntimeException("Unauthorized - email not found"));

        return postFoodJson(request, email);
    }
 
    @Override
    public ResponseEntity<String> postFoodJson(FoodPostJsonRequest request, String email) throws IOException {
        log.info("📥 Food post request received from: {}", email);

        // 1. Validate expiry date
        LocalDate today = LocalDate.now();
        LocalDate expiry;
        try {
            expiry = LocalDate.parse(request.getExpiryDate());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Invalid expiry date format. Use yyyy-MM-dd");
        }

        if (expiry.isBefore(today)) {
            return ResponseEntity.badRequest().body("❌ Expiry date cannot be in the past.");
        }

        // 2. Get authenticated donor
        User donor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("❌ Donor not found"));

        if (donor.getRole() == Role.NGO) {
            return ResponseEntity.status(403).body("❌ NGOs are not allowed to post food");
        }

        // 3. Create Food entity
        Food food = new Food();
        food.setFoodName(request.getFoodName());
        food.setQuantity(request.getQuantity());
        food.setRemainingQuantity(request.getQuantity());
        food.setExpiryDate(expiry);
        food.setDonor(donor);
        food.setStatus("POSTED");
        food.setLatitude(donor.getLatitude());
        food.setLongitude(donor.getLongitude());
        food.setDonorCertificateSent(false);
        food.setNgoCertificateSent(false);

        // 4. Safe hours if expiry today
        if (expiry.isEqual(today)) {
            if (request.getSafeHours() == null || request.getSafeHours() <= 0) {
                return ResponseEntity.badRequest().body("⏳ Since the expiry is today, please specify how many hours it's safe.");
            }
            food.setSafeHours(request.getSafeHours());
        } else {
            food.setSafeHours(null);
        }

        // 5. Optional image (base64)
        if (request.getImageBase64() != null && !request.getImageBase64().isBlank()) {
            byte[] imageBytes = Base64.getDecoder().decode(request.getImageBase64());
            String fileName = UUID.randomUUID() + "_food.jpg";

            Path staticDir = Paths.get("src/main/resources/static/food/");
            Files.createDirectories(staticDir);
            Path staticPath = staticDir.resolve(fileName);
            Files.write(staticPath, imageBytes);

            food.setImageUrl("/food/" + fileName);
        }

        // 6. Save food
        foodRepository.save(food);

        // 7. Notify NGOs within 60 km
        List<User> ngos = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.NGO &&
                        u.getLatitude() != null && u.getLongitude() != null)
                .collect(Collectors.toList());

        for (User ngo : ngos) {
            double distanceKm = distanceService.calculateDistance(
                    donor.getLatitude(), donor.getLongitude(),
                    ngo.getLatitude(), ngo.getLongitude()) / 1000.0;

            if (distanceKm <= 60.0) {
                emailService.sendNearbyDonationAlert(ngo, food, distanceKm);
            }
        }

        notificationService.notifyUser(
                donor,
                "✅ You posted a donation: '" + food.getFoodName() + "' with quantity " + food.getQuantity(),
                NotificationType.DONATION_POSTED);

        log.info("✅ Food posted successfully by: {}", donor.getEmail());
        return ResponseEntity.ok("✅ Food posted successfully via JSON.");
    }





    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireOldFoods() {
        List<Food> expired = foodRepository.findByStatusAndExpiryDateBefore("POSTED", LocalDate.now());
        for (Food f : expired) {
            f.setStatus("EXPIRED");
            foodRepository.save(f);
        }
    }

    @Override
    public List<FoodResponse> getMyDonations(String donorEmail) {
        User donor = userRepository.findByEmail(donorEmail)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        List<Food> foodList = foodRepository.findByDonor(donor);
        List<FoodResponse> responseList = new ArrayList<>();

        for (Food food : foodList) {
            FoodResponse response = new FoodResponse();
            response.setId(food.getId());
            response.setFoodName(food.getFoodName());
            response.setQuantity(food.getQuantity());
            response.setStatus(food.getStatus());
            response.setImageUrl(food.getImageUrl());
            response.setDonorName(donor.getName());

            if (food.getAcceptedBy() != null) {
                User ngo = food.getAcceptedBy();
                response.setAcceptedByName(ngo.getName());

                if (donor.getLatitude() != null && donor.getLongitude() != null &&
                        ngo.getLatitude() != null && ngo.getLongitude() != null) {
                    double distanceMeters = distanceService.calculateDistance(
                            donor.getLatitude(), donor.getLongitude(),
                            ngo.getLatitude(), ngo.getLongitude());
                    response.setDistanceKm(distanceMeters / 1000.0);
                    response.setGoogleMapsUrl(
                            FoodSearchResponse.generateGoogleMapsUrl(ngo.getLatitude(), ngo.getLongitude()));
                }
            } else {
                response.setDistanceKm(0.0);
                response.setGoogleMapsUrl(
                        FoodSearchResponse.generateGoogleMapsUrl(donor.getLatitude(), donor.getLongitude()));
            }

            responseList.add(response);
        }

        return responseList;
    }

    @Override
    public List<FoodSearchResponse> getAvailableFood(String ngoEmail, Double maxDistance) {
        User ngo = userRepository.findByEmail(ngoEmail)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        List<Food> availableFoods = foodRepository.findAvailableNonExpired();

        return availableFoods.stream()
                .map(f -> {
                    double distanceKm = 0.0;

                    if (f.getLatitude() != null && f.getLongitude() != null &&
                        ngo.getLatitude() != null && ngo.getLongitude() != null) {
                        distanceKm = distanceService.calculateDistance(
                                ngo.getLatitude(), ngo.getLongitude(),
                                f.getLatitude(), f.getLongitude()) / 1000.0;
                    }

                    LocalDateTime expiry = f.getExpiryDate() != null
                            ? f.getExpiryDate().atStartOfDay()
                            : null;

                    FoodSearchResponse dto = new FoodSearchResponse();
                    dto.setId(f.getId());
                    dto.setFoodName(f.getFoodName());
                    dto.setQuantity(f.getRemainingQuantity());
                    dto.setDistanceKm(distanceKm);
                    dto.setGoogleMapsUrl(FoodSearchResponse.generateGoogleMapsUrl(f.getLatitude(), f.getLongitude()));
                    dto.setExpiryDate(expiry);
                    
                    // ✅ Include safeHours if present
                    dto.setSafeHours(f.getSafeHours());

                    return dto;
                })
                .filter(dto -> maxDistance == null || dto.getDistanceKm() <= maxDistance)
                .collect(Collectors.toList());
    }


    @Override
    public ResponseEntity<String> acceptFood(Long foodId, String ngoEmail, int requestedQuantity) {
        if (ngoEmail == null || ngoEmail.isBlank()) {
            return ResponseEntity.badRequest().body("Email header is required.");
        }

        User ngo = userRepository.findByEmail(ngoEmail)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        if (!"NGO".equalsIgnoreCase(ngo.getRole().name())) {
            return ResponseEntity.status(403).body("Only NGOs are allowed to accept food.");
        }

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        if (!"POSTED".equals(food.getStatus()) || food.getRemainingQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Food not available for acceptance");
        }

        if (requestedQuantity > food.getRemainingQuantity()) {
            return ResponseEntity.badRequest()
                    .body("Requested quantity exceeds available amount: " + food.getRemainingQuantity());
        }

        // ✅ Record acceptance
        FoodAcceptance acceptance = new FoodAcceptance();
        acceptance.setFood(food);
        acceptance.setNgo(ngo);
        acceptance.setAcceptedQuantity(requestedQuantity);
        acceptance.setStatus("ACCEPTED");
        foodAcceptanceRepository.save(acceptance);

        // ✅ Update food
        food.setRemainingQuantity(food.getRemainingQuantity() - requestedQuantity);
        food.setAcceptedBy(ngo);
        food.setAcceptedByEmail(ngo.getEmail());
        food.setAcceptedDate(LocalDate.now());
        if (food.getRemainingQuantity() == 0) {
            food.setStatus("ACCEPTED");
        }
        foodRepository.save(food);

        // ✅ Update credits
        User donor = food.getDonor();
        donor.incrementPoints();
        ngo.incrementPoints();
        userRepository.saveAll(List.of(donor, ngo));

        // ✅ Distance for email and reporting
        double distanceKm = geoService.calculateDistance(
                donor.getLatitude(), donor.getLongitude(),
                ngo.getLatitude(), ngo.getLongitude());

        // ✅ Contact info mail to both (HTML + Maps)
     // ✅ Contact info mail to both (HTML + Maps)
        emailService.sendContactDetailsToBothParties(donor, ngo, food, distanceKm, acceptance);
        // ✅ Dashboard notifications
        notificationService.notifyUser(donor,
                ngo.getName() + " accepted " + requestedQuantity + " plates of your donation '" + food.getFoodName() + "'.",
                NotificationType.FOOD_ACCEPTED);

        notificationService.notifyUser(ngo,
                "You accepted " + requestedQuantity + " plates of '" + food.getFoodName() + "' from " + donor.getName() + ".",
                NotificationType.FOOD_ACCEPTED);

        notificationService.notifyUser(donor, "🎉 You earned 1 credit for your donation!", NotificationType.CREDIT_EARNED);
        notificationService.notifyUser(ngo, "🎉 You earned 1 credit for your acceptance!", NotificationType.CREDIT_EARNED);

        return ResponseEntity.ok("Accepted " + requestedQuantity + " plates");
    }


    @Override
    public List<NGODonationResponse> getMyAcceptedDonations(String ngoEmail) {
        User ngo = userRepository.findByEmail(ngoEmail)
                .orElseThrow(() -> new UsernameNotFoundException("NGO not found"));

        List<FoodAcceptance> acceptedList = foodAcceptanceRepository.findByNgo(ngo);

        return acceptedList.stream()
                .map(fa -> {
                    Food food = fa.getFood();
                    User donor = food.getDonor();

                    double distanceKm = 0.0;
                    if (donor.getLatitude() != null && donor.getLongitude() != null &&
                        ngo.getLatitude() != null && ngo.getLongitude() != null) {
                        distanceKm = distanceService.calculateDistance(
                                donor.getLatitude(), donor.getLongitude(),
                                ngo.getLatitude(), ngo.getLongitude()) / 1000.0;
                    }

                    return new NGODonationResponse(
                    	    food.getId(),
                    	    food.getFoodName(),
                    	    fa.getAcceptedQuantity(),
                    	    food.getStatus(),
                    	    food.getExpiryDate() != null ? food.getExpiryDate().toString() : "Unknown",
                    	    donor.getName() != null ? donor.getName() : "Unknown Donor",
                    	    donor.getEmail() != null ? donor.getEmail() : "Not provided",
                    	    donor.getPhone() != null ? donor.getPhone() : "Not provided",
                    	    donor.getAddress() != null ? donor.getAddress() : "Address unavailable",
                    	    food.getImageUrl() // ✅ image goes at the end
                    	);
                }).collect(Collectors.toList());
    }


    @Override
    public ResponseEntity<String> markDonationComplete(Long foodId, String ngoEmail) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        if (!"ACCEPTED".equals(food.getStatus())) {
            return ResponseEntity.badRequest().body("Only accepted donations can be marked as completed");
        }

        if (!food.getAcceptedBy().getEmail().equals(ngoEmail)) {
            return ResponseEntity.status(403).body("You are not authorized to complete this donation");
        }

        food.setStatus("COMPLETED");

        try {
            if (!food.isDonorCertificateSent() && food.getDonor() != null) {
                ByteArrayInputStream donorPdf = certificateService.generateDonorCertificatePdf(food.getDonor(), food);
                emailService.sendCertificate(
                    food.getDonor().getEmail(),
                    "Your Donation Certificate",
                    donorPdf,
                    "donor_certificate.pdf"
                );
                food.setDonorCertificateSent(true);

                // ✅ Notify Donor
                notificationService.notifyUser(
                        food.getDonor(),
                        "🎖 Your certificate for donating '" + food.getFoodName() + "' is ready to download!",
                        NotificationType.CERTIFICATE_READY
                );
            }

            if (!food.isNgoCertificateSent() && food.getAcceptedBy() != null) {
                int acceptedQty = getAcceptedQuantity(food, food.getAcceptedBy());
                ByteArrayInputStream ngoPdf = certificateService.generateNGOCertificatePdf(food.getAcceptedBy(), food, acceptedQty);
                emailService.sendCertificate(
                    food.getAcceptedBy().getEmail(),
                    "Your Acceptance Certificate",
                    ngoPdf,
                    "ngo_certificate.pdf"
                );
                food.setNgoCertificateSent(true);

                // ✅ Notify NGO
                notificationService.notifyUser(
                        food.getAcceptedBy(),
                        "🎖 Your certificate for accepting '" + food.getFoodName() + "' is ready to download!",
                        NotificationType.CERTIFICATE_READY
                );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        foodRepository.save(food);

        // ✅ Final Completion Notifications
        notificationService.notifyUser(
                food.getDonor(),
                "✅ Your donation '" + food.getFoodName() + "' is marked as completed.",
                NotificationType.GENERAL
        );

        notificationService.notifyUser(
                food.getAcceptedBy(),
                "✅ The donation you accepted '" + food.getFoodName() + "' is now completed.",
                NotificationType.GENERAL
        );

        return ResponseEntity.ok("Donation marked as completed and certificates sent");
    }

    private int getAcceptedQuantity(Food food, User ngo) {
        return foodAcceptanceRepository.findByFoodAndNgo(food, ngo)
                .map(FoodAcceptance::getAcceptedQuantity)
                .orElse(0);
    }
}
