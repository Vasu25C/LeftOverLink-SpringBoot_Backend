package com.leftoverlink.repository;

import com.leftoverlink.model.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    // You can add custom methods later, like:
    // List<CommunityPost> findByNgoEmail(String email);
}
