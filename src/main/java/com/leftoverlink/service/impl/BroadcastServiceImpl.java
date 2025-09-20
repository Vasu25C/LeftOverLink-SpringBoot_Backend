package com.leftoverlink.service.impl;

import com.leftoverlink.dto.BroadcastRequest;
import com.leftoverlink.model.EmailLog;
import com.leftoverlink.model.User;
import com.leftoverlink.repository.EmailLogRepository;
import com.leftoverlink.repository.UserRepository;
import com.leftoverlink.service.BroadcastService;
import com.leftoverlink.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BroadcastServiceImpl implements BroadcastService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Override
    public void broadcastToRole(BroadcastRequest request, String senderEmail) {
        List<User> targets;

        switch (request.getRole().toUpperCase()) {
            case "DONOR":
                targets = userRepository.findAll()
                        .stream().filter(u -> u.getRole().name().equals("DONOR")).toList();
                break;
            case "NGO":
                targets = userRepository.findAll()
                        .stream().filter(u -> u.getRole().name().equals("NGO")).toList();
                break;
            case "ALL":
            default:
                targets = userRepository.findAll();
        }

        for (User user : targets) {
            emailService.sendEmail(user.getEmail(), request.getSubject(), request.getMessage());
            emailLogRepository.save(new EmailLog(
                senderEmail,
                user.getEmail(),
                request.getSubject(),
                request.getMessage(),
                "BROADCAST"
            ));
        }
    }
}
