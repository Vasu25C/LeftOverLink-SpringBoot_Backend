package com.leftoverlink.service.impl;

import com.leftoverlink.service.EmailService;
import com.leftoverlink.service.OTPService;
import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.leftoverlink.service.OTPService;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

@Service
public class OTPServiceImpl implements OTPService {

    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, Boolean> otpValidated = new HashMap<>();

    private final EmailService emailService;

    public OTPServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public String generateAndSendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(9000) + 1000); // generates 4-digit OTP
        otpStore.put(email, otp);
        otpValidated.put(email, false); // Reset any previous status

        String subject = "LeftOverLink Email Verification OTP";
        String message = "Dear User,\n\nYour OTP for verification is: " + otp +
                "\n\nThank you,\nLeftOverLink Team";

        // ✅ SOP for debugging
        System.out.println("📤 Sending OTP to: " + email);
        System.out.println("🔐 Generated OTP: " + otp);
        System.out.println("🗃 Stored OTP in map: " + otpStore.get(email));

        emailService.sendEmail(email, subject, message);
        return otp;
    }

  /*  @Override
    public boolean validateOtp(String email, String otp) {
        boolean match = otp.equals(otpStore.get(email));
        if (match) {
            otpValidated.put(email, true);
        }
        return match;
    }*/
    @Override
    public boolean validateOtp(String email, String otp) {
        String storedOtp = otpStore.get(email);

        System.out.println("🔍 Validating OTP for email: " + email);
        System.out.println("📥 Provided OTP: " + otp);
        System.out.println("🧾 Stored OTP: " + storedOtp);

        if (storedOtp == null) {
            System.out.println("❌ No OTP found for email: " + email);
            return false;
        }

        if (!storedOtp.equals(otp)) {
            System.out.println("❌ OTP mismatch for email: " + email);
            return false;
        }

        otpValidated.put(email, true);
        System.out.println("✅ OTP verified successfully for " + email);
        return true;
    }

    public void debugOtpMap() {
        System.out.println("🧾 Current OTP Map: " + otpStore);
        System.out.println("✔️ Validation Status Map: " + otpValidated);
    }
 
    public String getOtpForEmail(String email) {
        return otpStore.get(email);
    }


    @Override
    public boolean isOtpValidated(String email) {
        return otpValidated.getOrDefault(email, false);
    }

    @Override
    public void markOtpValidated(String email) {
        otpValidated.put(email, true);
    }
}
