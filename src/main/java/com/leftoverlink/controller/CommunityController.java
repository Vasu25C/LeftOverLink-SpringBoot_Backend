package com.leftoverlink.controller;

import com.leftoverlink.dto.CommentRequest;
import com.leftoverlink.dto.CommunityPostRequest;
import com.leftoverlink.dto.CommunityPostResponse;
import com.leftoverlink.dto.ReactionRequest;
import com.leftoverlink.payload.ApiResponse;
import com.leftoverlink.service.CommunityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

	private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }
    @PostMapping(value = "/post", consumes = "multipart/form-data")
    public ResponseEntity<String> post(
            @RequestPart("caption") String caption,
            @RequestParam("image") MultipartFile image,
            Authentication authentication
    ) throws IOException {
        String email = authentication.getName();

        CommunityPostRequest request = new CommunityPostRequest();
        request.setCaption(caption);
        request.setImage(image);

        String result = communityService.createPost(request, email, image);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/comment")
    public ResponseEntity<String> addComment(
            @RequestBody CommentRequest request,
            Authentication authentication) {
        
        String email = authentication.getName(); // ✅ from JWT
        return ResponseEntity.ok(communityService.addComment(request, email));
    }

    @PostMapping("/react")
    public ResponseEntity<ApiResponse> react(@RequestBody ReactionRequest request, Authentication authentication) {
        String email = authentication.getName(); // Get email from JWT
        request.setReactedBy(email);             // Inject email into request

        String message = communityService.addReaction(request); // 💾 Call service to save

        return ResponseEntity.ok(new ApiResponse(true, message));
    }


    @GetMapping("/all")
    public ResponseEntity<List<CommunityPostResponse>> getAll() {
        return ResponseEntity.ok(communityService.getAllPosts());
    }
    
    @PutMapping(value = "/edit/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<String> editPost(
            @PathVariable Long postId,
            @RequestPart("caption") String caption,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication
    ) throws IOException {
        String email = authentication.getName();
        return ResponseEntity.ok(communityService.editPost(postId, caption, image, email));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(communityService.deletePost(postId, email));
    }


}
