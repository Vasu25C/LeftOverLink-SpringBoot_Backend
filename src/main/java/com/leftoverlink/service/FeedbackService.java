package com.leftoverlink.service;

import com.leftoverlink.dto.FeedbackRequest;
import com.leftoverlink.dto.FeedbackResponse;

import java.util.List;

public interface FeedbackService {
    void submitFeedback(FeedbackRequest request);
    List<FeedbackResponse> getMyFeedback();
}
