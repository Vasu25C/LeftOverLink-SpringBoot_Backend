package com.leftoverlink.service;

import com.leftoverlink.model.Food;
import com.leftoverlink.model.FoodAcceptance;
import com.leftoverlink.model.User;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.ByteArrayInputStream;

public interface CertificateService {
    ResponseEntity<InputStreamResource> generateDonorCertificate(User donor, Food food) throws IOException;
    ResponseEntity<InputStreamResource> generateNGOCertificate(User ngo, Food food, int quantity) throws IOException;

    // ✅ Add these methods
    ByteArrayInputStream generateDonorCertificatePdf(User donor, Food food) throws IOException;
    ByteArrayInputStream generateNGOCertificatePdf(User ngo, Food food, int quantity) throws IOException;
    public void generateDonorCertificate(FoodAcceptance acceptance);
    public void generateNgoCertificate(FoodAcceptance acceptance);


}
