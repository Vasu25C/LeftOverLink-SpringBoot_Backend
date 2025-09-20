package com.leftoverlink.controller;

import com.leftoverlink.dto.FeedbackRequest;
import com.leftoverlink.dto.FeedbackResponse;
import com.leftoverlink.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@PreAuthorize("hasRole('NGO') or hasRole('DONOR')")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<String> submit(@RequestBody FeedbackRequest request) {
        feedbackService.submitFeedback(request);
        return ResponseEntity.ok("Feedback submitted successfully.");
    }

    @GetMapping("/my")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedback() {
        return ResponseEntity.ok(feedbackService.getMyFeedback());
    }
}
