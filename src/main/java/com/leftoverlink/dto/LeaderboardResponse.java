package com.leftoverlink.dto;

import com.leftoverlink.model.User;
import java.util.List;

public class LeaderboardResponse {
    private List<UserSummary> topDonors;
    private List<UserSummary> topNgos;

    public LeaderboardResponse(List<User> donors, List<User> ngos) {
        this.topDonors = donors.stream().map(UserSummary::new).toList();
        this.topNgos = ngos.stream().map(UserSummary::new).toList();
    }

    public List<UserSummary> getTopDonors() {
        return topDonors;
    }

    public List<UserSummary> getTopNgos() {
        return topNgos;
    }

    // Inner static DTO to avoid exposing whole User entity
    public static class UserSummary {
        private String name;
        private String email;
        private int creditPoints;

        public UserSummary(User user) {
            this.name = user.getName();
            this.email = user.getEmail();
            this.creditPoints = user.getCreditPoints();
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public int getCreditPoints() {
            return creditPoints;
        }
    }
}
