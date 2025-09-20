package com.leftoverlink.controller;

import com.leftoverlink.model.Food;
import com.leftoverlink.model.FoodAcceptance;
import com.leftoverlink.repository.FoodAcceptanceRepository;
import com.leftoverlink.service.CertificateService;
import com.leftoverlink.service.FoodService;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/acceptance")
@RequiredArgsConstructor
public class FoodAcceptanceController {

    private final FoodAcceptanceRepository acceptanceRepo;
    private final CertificateService certificateService;
    private final FoodService foodService;
    
    public FoodAcceptanceController(FoodAcceptanceRepository acceptanceRepo,
            CertificateService certificateService,
            FoodService foodService) {
this.acceptanceRepo = acceptanceRepo;
this.certificateService = certificateService;
this.foodService = foodService;
}


    // ✅ Donor confirms they have handed over the food
    @PreAuthorize("hasRole('DONOR')")
    @PostMapping("/{id}/donor-confirm")
    public ResponseEntity<String> confirmByDonor(@PathVariable Long id, Authentication auth) {
        String email = auth.getName(); // From JWT
        FoodAcceptance acceptance = acceptanceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Acceptance not found"));

        if (!acceptance.getFood().getDonor().getEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(403).body("Unauthorized donor");
        }

        acceptance.setDonorConfirmed(true);
        acceptanceRepo.save(acceptance);

        autoCompleteIfEligible(acceptance);
        handleCertificateIfEligible(acceptance);

        return ResponseEntity.ok("Donor confirmation recorded.");
    }

    // ✅ NGO confirms they have received the food
    @PreAuthorize("hasRole('NGO')")
    @PostMapping("/{id}/ngo-confirm")
    public ResponseEntity<String> confirmByNgo(@PathVariable Long id, Authentication auth) {
        String email = auth.getName(); // From JWT
        FoodAcceptance acceptance = acceptanceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Acceptance not found"));

        if (!acceptance.getNgo().getEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(403).body("Unauthorized NGO");
        }

        acceptance.setNgoConfirmed(true);
        acceptanceRepo.save(acceptance);

        autoCompleteIfEligible(acceptance);
        handleCertificateIfEligible(acceptance);

        return ResponseEntity.ok("NGO confirmation recorded.");
    }

    // ✅ Download Donor Certificate (manual download)
    @GetMapping("/{id}/certificate/donor")
    public ResponseEntity<InputStreamResource> downloadDonorCert(@PathVariable Long id) {
        FoodAcceptance fa = acceptanceRepo.findById(id).orElseThrow();

        try {
            ByteArrayInputStream bis = certificateService.generateDonorCertificatePdf(
                    fa.getFood().getDonor(), fa.getFood()
            );
            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=donor_certificate.pdf")
                    .body(new InputStreamResource(bis));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // ✅ Trigger certificate generation only if both sides confirmed and not already sent
    private void handleCertificateIfEligible(FoodAcceptance acceptance) {
        Food food = acceptance.getFood();

        if (!acceptance.isDonorConfirmed() || !acceptance.isNgoConfirmed()) return;
        if (acceptance.isCertificateGenerated()) return;

        // Donor Certificate
        if (!acceptance.isDonorCertificateSent()) {
            certificateService.generateDonorCertificate(acceptance);
            acceptance.setDonorCertificateSent(true);
        }

        // NGO Certificate
        if (!acceptance.isNgoCertificateSent()) {
            certificateService.generateNgoCertificate(acceptance);
            acceptance.setNgoCertificateSent(true);
        }

        acceptance.setCertificateGenerated(true);
        acceptanceRepo.save(acceptance);
    }



    // ✅ Auto-complete logic
    private void autoCompleteIfEligible(FoodAcceptance acceptance) {
        Food food = acceptance.getFood();

        if (!"ACCEPTED".equals(food.getStatus()) || food.getRemainingQuantity() > 0) return;

        List<FoodAcceptance> allAcceptances = acceptanceRepo.findByFood(food);
        boolean allConfirmed = allAcceptances.stream()
                .allMatch(a -> a.isDonorConfirmed() && a.isNgoConfirmed());

        if (allConfirmed) {
            foodService.markDonationComplete(food.getId(), acceptance.getNgo().getEmail());
        }
    }
}
