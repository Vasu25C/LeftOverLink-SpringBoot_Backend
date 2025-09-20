package com.leftoverlink.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Replaces ngo field: supports NGO, DONOR, ADMIN
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private String caption;
    private String imageUrl;
    private LocalDateTime postedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reaction> reactions = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.postedAt = LocalDateTime.now();
    }

    // Utility methods
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void addReaction(Reaction reaction) {
        reactions.add(reaction);
        reaction.setPost(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }

    public void removeReaction(Reaction reaction) {
        reactions.remove(reaction);
        reaction.setPost(null);
    }
   /* private User ngo;
    public User getNgo() {
        return ngo;
    }

    public void setNgo(User ngo) {
        this.ngo = ngo;
    }
*/
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public static CommunityPost create(User user, String caption, String imageUrl, LocalDateTime postedAt) {
        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setCaption(caption);
        post.setImageUrl(imageUrl);
        post.setPostedAt(postedAt);
        return post;
    }
}
