package com.leftoverlink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commenterName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    private LocalDateTime commentedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private CommunityPost post;

    @PrePersist
    public void onCreate() {
        if (this.commentedAt == null) {
            this.commentedAt = LocalDateTime.now();
        }
    }

    // 🔨 Factory method (manual builder alternative)
    public static Comment create(CommunityPost post, String commenterName, String text, LocalDateTime commentedAt) {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setCommenterName(commenterName);
        comment.setText(text);
        comment.setCommentedAt(commentedAt);
        return comment;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(LocalDateTime commentedAt) {
        this.commentedAt = commentedAt;
    }

    public CommunityPost getPost() {
        return post;
    }

    public void setPost(CommunityPost post) {
        this.post = post;
    }
}
