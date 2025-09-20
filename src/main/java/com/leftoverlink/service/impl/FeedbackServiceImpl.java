package com.leftoverlink.service.impl;

import com.leftoverlink.dto.FeedbackRequest;
import com.leftoverlink.dto.FeedbackResponse;
import com.leftoverlink.model.Feedback;
import com.leftoverlink.model.User;
import com.leftoverlink.repository.FeedbackRepository;
import com.leftoverlink.service.FeedbackService;
import com.leftoverlink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserService userService;

    @Override
    public void submitFeedback(FeedbackRequest request) {
        User user = userService.getCurrentUser();

        Feedback feedback = new Feedback();
        feedback.setMessage(request.getMessage());
        feedback.setRating(request.getRating());
        feedback.setRole(user.getRole().name());
        feedback.setUser(user);
        feedback.setSubmittedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);
    }

    @Override
    public List<FeedbackResponse> getMyFeedback() {
        User user = userService.getCurrentUser();
        return feedbackRepository.findByUser(user).stream().map(f -> {
            FeedbackResponse res = new FeedbackResponse();
            res.setId(f.getId());
            res.setMessage(f.getMessage());
            res.setRating(f.getRating());
            res.setRole(f.getRole());
            res.setUsername(f.getUser().getName());
            res.setSubmittedAt(f.getSubmittedAt());
            return res;
        }).collect(Collectors.toList());
    }
}
