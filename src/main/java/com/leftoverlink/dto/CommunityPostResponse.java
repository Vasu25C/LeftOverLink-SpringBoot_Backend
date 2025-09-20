package com.leftoverlink.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for returning Community Post details along with summarized reactions and comments.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostResponse {

    private Long id;
    private String postedByName;
    private String caption;
    private String imageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime postedAt;

    // e.g., ["❤️ x3", "❤️ by Vasanth"]
    private List<String> reactions;

    // e.g., ["Vasanth: Great work!"]
    private List<String> comments;

    public static CommunityPostResponse create(Long id, String postedByName, String caption, String imageUrl,
                                               LocalDateTime postedAt, List<String> reactions, List<String> comments) {
        CommunityPostResponse r = new CommunityPostResponse();
        r.setId(id);
        r.setPostedByName(postedByName);
        r.setCaption(caption);
        r.setImageUrl(imageUrl);
        r.setPostedAt(postedAt);
        r.setReactions(reactions);
        r.setComments(comments);
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostedByName() {
        return postedByName;
    }

    public void setPostedByName(String postedByName) {
        this.postedByName = postedByName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public List<String> getReactions() {
        return reactions;
    }

    public void setReactions(List<String> reactions) {
        this.reactions = reactions;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
