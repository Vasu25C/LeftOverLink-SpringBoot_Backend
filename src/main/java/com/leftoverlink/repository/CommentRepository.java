package com.leftoverlink.repository;

import com.leftoverlink.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Optional: List<Comment> findByPostId(Long postId);
}
