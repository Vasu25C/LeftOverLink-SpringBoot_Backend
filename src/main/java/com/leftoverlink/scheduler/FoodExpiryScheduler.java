package com.leftoverlink.scheduler;

import com.leftoverlink.model.Food;
import com.leftoverlink.repository.FoodRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FoodExpiryScheduler {

    @Autowired
    private FoodRepository foodRepository;

    // ⏰ Runs every 1 hour
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void expireOldFoodPosts() {
        LocalDate today = LocalDate.now();

        List<Food> allPostedFoods = foodRepository.findByStatus("POSTED");

        List<Food> toExpire = allPostedFoods.stream()
                .filter(food -> {
                    LocalDate expiryDate = food.getExpiryDate();

                    // Case 1: Expired by date
                    if (expiryDate.isBefore(today)) return true;

                    // Case 2: Expiring today, and safeHours exceeded
                    if (expiryDate.isEqual(today) && food.getSafeHours() != null) {
                        LocalDateTime expiryTime = expiryDate.atStartOfDay().plusHours(food.getSafeHours());
                        return LocalDateTime.now().isAfter(expiryTime);
                    }

                    return false;
                })
                .collect(Collectors.toList());

        for (Food food : toExpire) {
            food.setStatus("EXPIRED");
            System.out.println("⛔ Expired food ID " + food.getId() + " -> " + food.getFoodName());
        }

        foodRepository.saveAll(toExpire);
    }
}
