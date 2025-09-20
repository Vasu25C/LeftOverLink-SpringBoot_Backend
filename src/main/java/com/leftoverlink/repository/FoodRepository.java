package com.leftoverlink.repository;

import com.leftoverlink.model.Food;
import com.leftoverlink.model.FoodAcceptance;
import com.leftoverlink.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByDonorEmail(String email);
    List<Food> findByStatus(String status);
    List<Food> findByAcceptedByAndStatus(User acceptedBy, String status);
    long countByStatus(String status);
    Page<Food> findByFoodNameContainingIgnoreCaseOrStatusContainingIgnoreCase(String foodName, String status, Pageable pageable);

    @Query("SELECT f FROM Food f WHERE f.status = 'POSTED' AND f.expiryDate >= CURRENT_DATE")
    List<Food> findAvailableNonExpired();

    Optional<Food> findTopByDonorOrderByIdDesc(User donor);

    List<Food> findByStatusAndExpiryDateBefore(String status, LocalDate date);
    
    List<Food> findByDonor(User donor);
    @Query("SELECT SUM(f.quantity) FROM Food f WHERE f.status = 'COMPLETED'")
    Long sumCompletedFoodQuantities();


}
