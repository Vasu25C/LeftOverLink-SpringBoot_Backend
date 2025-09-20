package com.leftoverlink.service.impl;

import com.leftoverlink.dto.LeaderboardEntry;
import com.leftoverlink.model.Role; // ✅ <== Add this here
import com.leftoverlink.model.User;
import com.leftoverlink.repository.UserRepository;
import com.leftoverlink.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

	private final UserRepository userRepository;

	public LeaderboardServiceImpl(UserRepository userRepository) {
	    this.userRepository = userRepository;
	}

    @Override
    public List<LeaderboardEntry> getLeaderboard() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        // DONORs
        List<User> donors = userRepository.findTop5ByRoleOrderByCreditPointsDesc(Role.DONOR);
        for (User donor : donors) {
            leaderboard.add(new LeaderboardEntry(donor.getName(), "DONOR", donor.getCreditPoints()));
        }

        // NGOs
        List<User> ngos = userRepository.findTop5ByRoleOrderByCreditPointsDesc(Role.NGO);
        for (User ngo : ngos) {
            leaderboard.add(new LeaderboardEntry(ngo.getName(), "NGO", ngo.getCreditPoints()));
        }

        return leaderboard;
    }
}
