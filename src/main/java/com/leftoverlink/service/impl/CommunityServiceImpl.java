package com.leftoverlink.service.impl;

import com.leftoverlink.dto.*;
import com.leftoverlink.model.*;
import com.leftoverlink.repository.*;
import com.leftoverlink.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final UserRepository userRepository;
    private final CommunityPostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;

    public CommunityServiceImpl(UserRepository userRepository,
            CommunityPostRepository postRepository,
            CommentRepository commentRepository,
            ReactionRepository reactionRepository) {
this.userRepository = userRepository;
this.postRepository = postRepository;
this.commentRepository = commentRepository;
this.reactionRepository = reactionRepository;
}
 
    @Override
    public String createPost(CommunityPostRequest request, String email, MultipartFile image) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setCaption(request.getCaption());
        post.setPostedAt(LocalDateTime.now());

        if (image != null && !image.isEmpty()) {
            String fileName = saveImage(image);
            post.setImageUrl("/community/" + fileName);
        }

        postRepository.save(post);
        return "✅ Community post created successfully.";
    }

    @Override
    public String addComment(CommentRequest request, String email) {
        CommunityPost post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Community post not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setCommenterName(user.getName());
        comment.setText(request.getText());
        comment.setCommentedAt(LocalDateTime.now());

        commentRepository.save(comment);
        return "💬 Comment added.";
    }

    @Override
    public String addReaction(ReactionRequest request) {
        CommunityPost post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Community post not found"));

        Reaction reaction = new Reaction();
        reaction.setPost(post);
        reaction.setType(request.getType());
        reaction.setReactedBy(request.getReactedBy());
        reaction.setReactedByEmail(request.getReactedBy());

        reactionRepository.save(reaction);
        return "❤️ Reaction added.";
    }

    @Override
    public List<CommunityPostResponse> getAllPosts() {
        return postRepository.findAll().stream().map(post -> {
            List<String> reactions = post.getReactions().stream()
                    .map(r -> r.getType() + " by " + r.getReactedBy())
                    .collect(Collectors.toList());

            List<String> comments = post.getComments().stream()
                    .map(c -> c.getCommenterName() + ": " + c.getText())
                    .collect(Collectors.toList());

            return CommunityPostResponse.create(
                    post.getId(),
                    post.getUser().getName(),
                    post.getCaption(),
                    post.getImageUrl(),
                    post.getPostedAt(),
                    reactions,
                    comments
            );
        }).collect(Collectors.toList());
    }

    @Override
    public String editPost(Long postId, String caption, MultipartFile image, String email) throws IOException {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isOwner = post.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().name().equalsIgnoreCase("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new SecurityException("Unauthorized to edit this post.");
        }

        if (caption != null && !caption.isBlank()) {
            post.setCaption(caption);
        }

        if (image != null && !image.isEmpty()) {
            String fileName = saveImage(image);
            post.setImageUrl("/community/" + fileName);
        }

        postRepository.save(post);
        return "✏️ Post updated successfully.";
    }

    @Override
    public String deletePost(Long postId, String email) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isOwner = post.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().name().equalsIgnoreCase("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new SecurityException("Unauthorized to delete this post.");
        }

        commentRepository.deleteAll(post.getComments());
        reactionRepository.deleteAll(post.getReactions());
        postRepository.delete(post);

        return "🗑️ Post deleted successfully.";
    }

    private String saveImage(MultipartFile image) throws IOException {
        String extension = Optional.ofNullable(image.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf('.')))
                .orElse(".jpg");

        String fileName = UUID.randomUUID() + "_community" + extension;
        Path uploadPath = Paths.get("src/main/resources/static/community", fileName);
        Files.createDirectories(uploadPath.getParent());
        Files.write(uploadPath, image.getBytes());
        return fileName;
    }
}
