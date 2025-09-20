package com.leftoverlink.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentRequest {

    @NotNull(message = "Post ID is required")
    @Min(value = 1, message = "Post ID must be positive")
    private Long postId;

    @NotBlank(message = "Comment text cannot be blank")
    private String text;

    // Will be set from the authenticated user via controller
 //   private String email;

    public CommentRequest() {
    }

    public CommentRequest(Long postId, String text, String email) {
        this.postId = postId;
        this.text = text;
     //   this.email = email;
    }

    // ✅ Factory method (optional)
    public static CommentRequest of(Long postId, String text, String email) {
        CommentRequest r = new CommentRequest();
        r.setPostId(postId);
        r.setText(text);
      //  r.setEmail(email);
        return r;
    }

    // Getters and Setters
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

   /* public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }*/
}
