package com.leftoverlink.service.impl;

import com.leftoverlink.repository.FoodRepository;
import com.leftoverlink.repository.UserRepository;
import com.leftoverlink.repository.AcceptedDonationRepository;
import com.leftoverlink.model.Role;
import com.leftoverlink.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private AcceptedDonationRepository acceptedDonationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public long getMealsShared() {
        // Assume each accepted donation counts as one meal.
        return acceptedDonationRepository.countTotalMealsShared();
    }

    @Override
    public long getActiveDonors() {
        return userRepository.countByRole(Role.DONOR);
    }

    @Override
    public long getPartnerNGOs() {
        return userRepository.countByRole(Role.NGO);
    }

    @Override
    public long getCitiesCovered() {
        return userRepository.countDistinctCities();
    }
    
   

}
