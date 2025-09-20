package com.leftoverlink.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionRequest {

    @NotNull(message = "Post ID is required")
    @Min(value = 1, message = "Post ID must be a positive number")
    private Long postId;

 
    
    private String reactedBy; // could be email, name, or user ID

    @NotBlank(message = "Reaction type is required")
    private String type; // e.g., ❤️, 👍, 👏

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public String getReactedBy() {
		return reactedBy;
	}

	public void setReactedBy(String reactedBy) {
		this.reactedBy = reactedBy;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
    
}
