package com.leftoverlink.service;

import com.leftoverlink.dto.LeaderboardEntry;
import java.util.List;

public interface LeaderboardService {
    List<LeaderboardEntry> getLeaderboard();
}
