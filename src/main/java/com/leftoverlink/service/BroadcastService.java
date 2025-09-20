package com.leftoverlink.service;

import com.leftoverlink.dto.BroadcastRequest;

public interface BroadcastService {
    void broadcastToRole(BroadcastRequest request, String senderEmail);
}
