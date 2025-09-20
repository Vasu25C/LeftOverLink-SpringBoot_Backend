package com.leftoverlink.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public class CommunityPostRequest {

    @NotBlank(message = "Caption is required")
    private String caption;

    // Will be automatically bound by Spring when used in @ModelAttribute or @RequestPart
    private MultipartFile image;

    public CommunityPostRequest() {
    }

    public CommunityPostRequest(String caption, MultipartFile image) {
        this.caption = caption;
        this.image = image;
    }

    // ✅ Factory method (optional)
    public static CommunityPostRequest of(String caption, MultipartFile image) {
        CommunityPostRequest req = new CommunityPostRequest();
        req.setCaption(caption);
        req.setImage(image);
        return req;
    }

    // Getters and Setters
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
