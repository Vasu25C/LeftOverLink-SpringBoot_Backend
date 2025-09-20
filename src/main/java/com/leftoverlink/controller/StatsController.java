package com.leftoverlink.controller;

import com.leftoverlink.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("mealsShared", statsService.getMealsShared());
        stats.put("activeDonors", statsService.getActiveDonors());
        stats.put("partnerNGOs", statsService.getPartnerNGOs());
        stats.put("citiesCovered", statsService.getCitiesCovered());
        return ResponseEntity.ok(stats);
    }
}
