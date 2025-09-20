package com.leftoverlink.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // Example: "❤️", "👍"

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private CommunityPost post;

    @Column(nullable = false)
    private String reactedByEmail; // Email of the user who reacted

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    private String reactedBy;

    public String getReactedBy() {
        return reactedBy;
    }

    public void setReactedBy(String reactedBy) {
        this.reactedBy = reactedBy;
    }

    
    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public CommunityPost getPost() {
		return post;
	}


	public void setPost(CommunityPost post) {
		this.post = post;
	}


	public String getReactedByEmail() {
		return reactedByEmail;
	}


	public void setReactedByEmail(String reactedByEmail) {
		this.reactedByEmail = reactedByEmail;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	// ✅ Simple factory method
    public static Reaction create(CommunityPost post, String type, String reactedByEmail) {
        Reaction reaction = new Reaction();
        reaction.setPost(post);
        reaction.setType(type);
        reaction.setReactedByEmail(reactedByEmail);
        return reaction;
    }
}
