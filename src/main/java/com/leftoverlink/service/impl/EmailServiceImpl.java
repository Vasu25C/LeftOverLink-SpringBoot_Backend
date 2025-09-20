package com.leftoverlink.service.impl;

import com.leftoverlink.model.Food;
import com.leftoverlink.model.FoodAcceptance;
import com.leftoverlink.model.Role;
import com.leftoverlink.model.User;
import com.leftoverlink.repository.EmailLogRepository;
import com.leftoverlink.repository.UserRepository;
import com.leftoverlink.service.EmailService;
import com.leftoverlink.service.GeoService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import com.leftoverlink.repository.EmailLogRepository;
import com.leftoverlink.repository.UserRepository;
import com.leftoverlink.service.GeoService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;
    private final UserRepository userRepository;
    private final GeoService geoService;

    public EmailServiceImpl(JavaMailSender mailSender,
            EmailLogRepository emailLogRepository,
            UserRepository userRepository,
            GeoService geoService) {
this.mailSender = mailSender;
this.emailLogRepository = emailLogRepository;
this.userRepository = userRepository;
this.geoService = geoService;
}
    
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("✅ Sent HTML email to: " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("❌ Failed to send email to " + to, e);
        }
    }

    @Override
    public void sendWelcomeEmailWithInstructions(User user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(user.getEmail());
        helper.setSubject("🎉 Welcome to LeftOverLink – Let’s End Hunger Together!");
        helper.setText(buildWelcomeHtml(user.getName()), true);
        helper.addInline("logo", new ClassPathResource("static/images/logo.png"));
        helper.addInline("signature", new ClassPathResource("static/images/signature.png"));

        mailSender.send(message);
    }

    private String buildWelcomeHtml(String name) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>Dear <b>%s</b>,</h2>
                <p>Welcome to <b>LeftOverLink</b> – your step toward a zero-waste, hunger-free society! 💚</p>
                <h3>🌍 What Is LeftOverLink?</h3>
                <p>We connect Donors with NGOs to ensure leftover food reaches those in need. 🍱❤️</p>
                <h3>🔰 How to Use This Platform</h3>
                <p><b>For Donors:</b> Post food → Track → Hand over → Get certificate → Earn credits</p>
                <p><b>For NGOs:</b> Search food → Accept partial/full → Coordinate → Mark complete → Earn credit</p>
                <h3>📢 Notifications:</h3>
                <p>Track food status, credits, certificates in your dashboard notifications.</p>
                <h3>🌐 Community Tab:</h3>
                <p>Post photos, like, react, and comment. Spread positivity and impact!</p>
                <h3>📬 Need Help?</h3>
                <p>Contact us anytime via the Contact tab or reply to this email.</p>
                <br>
                <img src='cid:logo' width='100'/><br><br>
                <p style="font-style: italic;">Warm regards,</p>
                <p><b>Dr. Nagarathna</b><br/>Founder, LeftOverLink</p>
                <img src='cid:signature' width='120'/>
            </body>
            </html>
        """.formatted(name);
    }

    @Override
    public void sendFoodAlertToNearbyNGOs(Food food) {
        User donor = food.getDonor();
        List<User> allNGOs = userRepository.findByRole(Role.NGO);

        for (User ngo : allNGOs) {
            double distanceKm = geoService.calculateDistance(
                    donor.getLatitude(), donor.getLongitude(),
                    ngo.getLatitude(), ngo.getLongitude());

            if (distanceKm <= 60.0) {
                sendNearbyDonationAlert(ngo, food, distanceKm);
            }
        }
    }

    @Override
    public void sendNearbyDonationAlert(User ngo, Food food, double distanceKm) {
        String donorName = food.getDonor().getName();
        String foodName = food.getFoodName();
        int quantity = food.getQuantity();
        String expiry = food.getExpiryDate().toString();
        String foodImage = food.getImageUrl() != null ? "https://yourdomain.com" + food.getImageUrl() : "";
        String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + food.getLatitude() + "," + food.getLongitude();

        String body = String.format("""
            Dear %s Team,

            🌟 A new food donation has just been posted near your location (%.2f km away):

            • Food: %s
            • Quantity: %d plates
            • Expiry Date: %s
            • Donor: %s

            📍 [View Location](%s)

            %s

            Together, let us prevent hunger and food waste. Your swift action can make a big difference.

            Regards,  
            Dr. Nagarathna  
            Founder, LeftOverLink
        """,
        ngo.getName(), distanceKm, foodName, quantity, expiry, donorName, googleMapsUrl,
        foodImage.isEmpty() ? "" : "\n🖼 Food Image: " + foodImage);

        sendEmail(ngo.getEmail(), "Nearby Food Donation Opportunity", body);
    }

    @Override
    public void sendHtmlEmailWithInlineImages(String to, String subject, String html, Map<String, String> inlineResources) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            for (Map.Entry<String, String> entry : inlineResources.entrySet()) {
                FileSystemResource resource = new FileSystemResource(new File(entry.getValue()));
                helper.addInline(entry.getKey(), resource);
            }

            mailSender.send(message);
            System.out.println("✅ HTML email with inline images sent to " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send HTML email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendWelcomeEmail(User user) {
        String subject = "Welcome to LeftOverLink! ❤️";
        String html = """
            <html>
            <body style='font-family: Arial, sans-serif; padding: 20px;'>
                <img src='cid:logo' height='80'/>
                <h2>Dear <b>%s</b>,</h2>
                <p>Welcome to <b>LeftOverLink</b>! Your journey to reduce food waste starts here.</p>
                <p>We're thrilled to have you with us. Together, we can ensure no food is left behind and no stomach goes empty.</p>
                <p style='font-style: italic;'>“Your small step can make a huge difference.”</p>
                <p>
                    <img src='cid:signature' style='width: 120px; height: auto; margin-bottom: 4px;'/>
                    <b>Dr. Nagarathna</b><br/>
                    <i>Founder, LeftOverLink</i>
                </p>
            </body>
            </html>
        """.formatted(user.getName());

        sendHtmlEmailWithInlineImages(user.getEmail(), subject, html, Map.of(
                "logo", "src/main/resources/static/images/logo.png",
                "signature", "src/main/resources/static/images/signature.png"));
    }

    @Override
    public void sendContactDetailsToBothParties(User donor, User ngo, Food food, double distanceKm, FoodAcceptance acceptance) {
        String donorHtml = String.format("""
                <h2>📦 Your food was accepted!</h2>
                <p>Here are the NGO's contact details:</p>
                <ul>
                    <li><b>Name:</b> %s</li>
                    <li><b>Email:</b> %s</li>
                    <li><b>Phone:</b> %s</li>
                    <li><b>Address:</b> %s</li>
                    <li><b>Google Maps:</b> <a href="%s">View NGO Location</a></li>
                    <li><b>Distance:</b> %.2f km</li>
                </ul>
                <p><b>Acceptance ID:</b> %d</p>
                <p style="color:darkblue;">🔔 Once you hand over the food to NGO, please <b>log in</b> and mark the donation as <b>Completed</b>.</p>
            """, ngo.getName(), ngo.getEmail(), ngo.getPhone(), ngo.getAddress(),
                "https://www.google.com/maps/search/?api=1&query=" + ngo.getLatitude() + "," + ngo.getLongitude(),
                distanceKm, acceptance.getId());

        String ngoHtml = String.format("""
                <h2>🙏 You accepted a food donation!</h2>
                <p>Here are the Donor's contact details:</p>
                <ul>
                    <li><b>Name:</b> %s</li>
                    <li><b>Email:</b> %s</li>
                    <li><b>Phone:</b> %s</li>
                    <li><b>Address:</b> %s</li>
                    <li><b>Google Maps:</b> <a href="%s">View Donor Location</a></li>
                    <li><b>Distance:</b> %.2f km</li>
                </ul>
                <p><b>Acceptance ID:</b> %d</p>
                <p style="color:darkblue;">🔔 After receiving the food, please <b>log in</b> and mark it as <b>Completed</b>.</p>
            """, donor.getName(), donor.getEmail(), donor.getPhone(), donor.getAddress(),
                "https://www.google.com/maps/search/?api=1&query=" + donor.getLatitude() + "," + donor.getLongitude(),
                distanceKm, acceptance.getId());

        sendHtmlEmail(donor.getEmail(), "📦 Your Food Was Accepted! Contact NGO", donorHtml);
        sendHtmlEmail(ngo.getEmail(), "📦 Contact Details of Donor for Received Food", ngoHtml);
    }

    @Override
    public void sendCertificate(String to, String subject, ByteArrayInputStream pdf, String filename) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText("Please find attached your certificate from LeftOverLink.");
            helper.addAttachment(filename, new ByteArrayDataSource(pdf, "application/pdf"));
            mailSender.send(message);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("anumalerv@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
