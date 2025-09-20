package com.leftoverlink.repository;

import com.leftoverlink.model.Food;
import com.leftoverlink.model.FoodAcceptance;
import com.leftoverlink.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodAcceptanceRepository extends JpaRepository<FoodAcceptance, Long> {
    List<FoodAcceptance> findByNgoEmail(String email);
    List<FoodAcceptance> findByFoodId(Long foodId);
    Optional<FoodAcceptance> findByFoodAndNgo(Food food, User ngo);
    List<FoodAcceptance> findByNgo(User ngo);

    Optional<FoodAcceptance> findTopByNgoOrderByIdDesc(User ngo);
        List<FoodAcceptance> findByFood(Food food);
    

}
