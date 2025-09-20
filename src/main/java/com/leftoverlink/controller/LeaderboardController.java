package com.leftoverlink.controller;

import com.leftoverlink.dto.LeaderboardResponse;
import com.leftoverlink.model.Role;
import com.leftoverlink.model.User;
import com.leftoverlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

	private final UserRepository userRepository;

	public LeaderboardController(UserRepository userRepository) {
	    this.userRepository = userRepository;
	}

    @GetMapping("/leaderboard")
    public LeaderboardResponse getLeaderboard() {
        List<User> donors = userRepository.findTop5ByRoleOrderByCreditPointsDesc(Role.DONOR);
        List<User> ngos = userRepository.findTop5ByRoleOrderByCreditPointsDesc(Role.NGO);
        return new LeaderboardResponse(donors, ngos);
    }
}
