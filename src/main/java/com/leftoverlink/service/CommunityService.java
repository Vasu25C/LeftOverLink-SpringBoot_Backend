package com.leftoverlink.service;

import com.leftoverlink.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CommunityService {

    String createPost(CommunityPostRequest request, String email, MultipartFile image) throws IOException;

    String addComment(CommentRequest request, String email);

    String addReaction(ReactionRequest request);

    String editPost(Long postId, String updatedCaption, MultipartFile updatedImage, String email) throws IOException;

    String deletePost(Long postId, String email);

    List<CommunityPostResponse> getAllPosts();
}
