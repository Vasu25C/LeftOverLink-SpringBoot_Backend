package com.leftoverlink.repository;

import com.leftoverlink.model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    // Optional: List<Reaction> findByPostId(Long postId);
}
