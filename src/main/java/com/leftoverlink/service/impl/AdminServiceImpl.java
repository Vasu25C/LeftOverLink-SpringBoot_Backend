package com.leftoverlink.service.impl;

import com.leftoverlink.dto.FoodSearchResponse;
import com.leftoverlink.dto.FoodStatusStatsResponse;
import com.leftoverlink.dto.UserSearchResponse;
import com.leftoverlink.model.Food;
import com.leftoverlink.model.User;
import com.leftoverlink.repository.FoodRepository;
import com.leftoverlink.repository.UserRepository;
import com.leftoverlink.service.AdminService;
import com.leftoverlink.service.DistanceService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.domain.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final DistanceService distanceService;
    private final JavaMailSender mailSender;
	 public AdminServiceImpl(UserRepository userRepository,
             FoodRepository foodRepository,
             DistanceService distanceService,
             JavaMailSender mailSender) {
this.userRepository = userRepository;
this.foodRepository = foodRepository;
this.distanceService = distanceService;
this.mailSender = mailSender;
}

    @Override
    public void emailCsvReportToAdmin() throws IOException, MessagingException {
        List<User> users = userRepository.findAll();
        List<Food> foods = foodRepository.findAll();

        StringBuilder usersCsv = new StringBuilder("ID,Name,Email,Role,Address,Pincode\n");
        for (User user : users) {
            usersCsv.append(String.format("%d,%s,%s,%s,%s,%s\n",
                    user.getId(), escape(user.getName()), user.getEmail(), user.getRole().name(),
                    escape(user.getAddress()), user.getPincode()));
        }

        StringBuilder foodsCsv = new StringBuilder("ID,Food Name,Quantity,Status,Donor,Accepted By,Expiry Date\n");
        for (Food food : foods) {
            foodsCsv.append(String.format("%d,%s,%d,%s,%s,%s,%s\n",
                    food.getId(), escape(food.getFoodName()), food.getQuantity(),
                    food.getStatus(), food.getDonor().getName(),
                    food.getAcceptedBy() != null ? food.getAcceptedBy().getName() : "",
                    food.getExpiryDate()));
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo("admin@example.com"); // Replace with actual admin email
        helper.setSubject("LeftOverLink Daily CSV Report");
        helper.setText("Dear Admin,\n\nPlease find attached the latest user and food reports.\n\nThanks,\nLeftOverLink");

        InputStreamSource usersSource = () -> new ByteArrayInputStream(usersCsv.toString().getBytes());
        InputStreamSource foodsSource = () -> new ByteArrayInputStream(foodsCsv.toString().getBytes());

        helper.addAttachment("users.csv", usersSource);
        helper.addAttachment("foods.csv", foodsSource);

        mailSender.send(message);
    }

    @Override
    public void exportUsersCsv(HttpServletResponse response) throws IOException {
        List<User> users = userRepository.findAll();

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=users.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Name,Email,Role,Address,Pincode");

        for (User user : users) {
            writer.printf("%d,%s,%s,%s,%s,%s\n",
                    user.getId(),
                    escape(user.getName()),
                    user.getEmail(),
                    user.getRole().name(),
                    escape(user.getAddress()),
                    user.getPincode());
        }

        writer.flush();
        writer.close();
    }

    @Override
    public void exportFoodsCsv(HttpServletResponse response) throws IOException {
        List<Food> foods = foodRepository.findAll();

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=foods.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Food Name,Quantity,Status,Donor,Accepted By,Expiry Date");

        for (Food food : foods) {
            writer.printf("%d,%s,%d,%s,%s,%s,%s\n",
                    food.getId(),
                    escape(food.getFoodName()),
                    food.getQuantity(),
                    food.getStatus(),
                    food.getDonor().getName(),
                    food.getAcceptedBy() != null ? food.getAcceptedBy().getName() : "",
                    food.getExpiryDate() != null ? food.getExpiryDate().toString() : "");
        }

        writer.flush();
        writer.close();
    }

    private String escape(String val) {
        return val == null ? "" : val.replace(",", " "); // basic CSV escaping
    }

    @Override
    public Page<FoodSearchResponse> searchFoodPosts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Food> foods = foodRepository
                .findByFoodNameContainingIgnoreCaseOrStatusContainingIgnoreCase(query, query, pageable);

        return new PageImpl<>(
                foods.getContent().stream()
                .map(f -> new FoodSearchResponse(
                	    f.getId(),
                	    f.getFoodName(),
                	    f.getQuantity(),
                	    f.getStatus(),
                	    f.getDonor().getName(),
                	    f.getAcceptedBy() != null ? f.getAcceptedBy().getName() : null,
                	    calculateDistance(f),
                	    generateGoogleMapsUrl(f),
                	    parseExpiryDate(f.getExpiryDate())  // ✅ Convert String → LocalDateTime
                	))
                        .collect(Collectors.toList()),
                pageable,
                foods.getTotalElements()
        );
    }

    private LocalDateTime parseExpiryDate(LocalDate expiryDate) {
        return expiryDate != null ? expiryDate.atStartOfDay() : null;
    }



    private double calculateDistance(Food f) {
        if (f.getAcceptedBy() != null && f.getLatitude() != null && f.getLongitude() != null) {
            String donorLat = f.getLatitude();
            String donorLon = f.getLongitude();
            String ngoLat = f.getAcceptedBy().getLatitude();
            String ngoLon = f.getAcceptedBy().getLongitude();

            if (ngoLat != null && ngoLon != null) {
                return distanceService.calculateDistance(ngoLat, ngoLon, donorLat, donorLon) / 1000.0;
            }
        }
        return 0.0;
    }
    
    private String generateGoogleMapsUrl(Food food) {
        if (food.getLatitude() != null && food.getLongitude() != null) {
            return "https://www.google.com/maps?q=" + food.getLatitude() + "," + food.getLongitude();
        }
        return null;
    }

    @Override
    public List<Food> getNearbyFood(String ngoEmail, double maxDistanceKm) {
        User ngo = userRepository.findByEmail(ngoEmail)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        List<Food> postedFood = foodRepository.findByStatus("POSTED");

        return postedFood.stream().filter(food -> {
            if (food.getLatitude() == null || food.getLongitude() == null) return false;
            double dist = distanceService.calculateDistance(
                    ngo.getLatitude(), ngo.getLongitude(),
                    food.getLatitude(), food.getLongitude()
            );
            return dist / 1000 <= maxDistanceKm;
        }).collect(Collectors.toList());
    }

    @Override
    public FoodStatusStatsResponse getFoodStatusStats() {
        long posted = foodRepository.countByStatus("POSTED");
        long accepted = foodRepository.countByStatus("ACCEPTED");
        long completed = foodRepository.countByStatus("COMPLETED");
        long expired = foodRepository.countByStatus("EXPIRED");

        return new FoodStatusStatsResponse(posted, accepted, completed, expired);
    }

    @Override
    public Page<UserSearchResponse> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<User> users = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable);

        return new PageImpl<>(
                users.getContent()
                        .stream()
                        .map(u -> new UserSearchResponse(u.getName(), u.getEmail(), u.getPhone(), u.getRole()))
                        .collect(Collectors.toList()),
                pageable,
                users.getTotalElements()
        );
    }
}
