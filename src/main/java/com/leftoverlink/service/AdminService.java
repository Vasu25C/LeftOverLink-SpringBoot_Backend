package com.leftoverlink.service;

import com.leftoverlink.dto.FoodSearchResponse;
import com.leftoverlink.dto.FoodStatusStatsResponse;
import com.leftoverlink.dto.UserSearchResponse;
import com.leftoverlink.model.Food; // ✅ Add this

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;

public interface AdminService {
    Page<UserSearchResponse> searchUsers(String query, int page, int size);
    Page<FoodSearchResponse> searchFoodPosts(String query, int page, int size);
    FoodStatusStatsResponse getFoodStatusStats();
    void exportUsersCsv(HttpServletResponse response) throws IOException;
    void exportFoodsCsv(HttpServletResponse response) throws IOException;
    void emailCsvReportToAdmin() throws IOException, MessagingException;

    List<Food> getNearbyFood(String ngoEmail, double maxDistanceKm); // ✅ This is fine now
}
