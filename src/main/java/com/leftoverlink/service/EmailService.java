package com.leftoverlink.service;

import com.leftoverlink.model.Food;
import com.leftoverlink.model.User;
import com.leftoverlink.model.FoodAcceptance;

import jakarta.mail.MessagingException;

import java.io.ByteArrayInputStream;
import java.util.Map;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendCertificate(String to, String subject, ByteArrayInputStream pdf, String filename);
    void sendNearbyDonationAlert(User ngo, Food food, double distanceKm);
    void sendFoodAlertToNearbyNGOs(Food food);
    void sendHtmlEmailWithInlineImages(String to, String subject, String html, Map<String, String> inlineResources);
    void sendWelcomeEmail(User user);
 // EmailService.java
    void sendContactDetailsToBothParties(User donor, User ngo, Food food, double distanceKm, FoodAcceptance acceptance);
    void sendWelcomeEmailWithInstructions(User user) throws MessagingException;
    void sendHtmlEmail(String to, String subject, String htmlContent);
   // ✅ Newly added methods
     //void sendAcceptanceMailToDonor(User donor, User ngo, FoodAcceptance acceptance);
    // void sendAcceptanceMailToNGO(User ngo, User donor, FoodAcceptance acceptance);
}
