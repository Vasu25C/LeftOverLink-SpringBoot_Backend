package com.leftoverlink.service.impl;

import com.leftoverlink.model.User;
import com.leftoverlink.repository.UserRepository;
import com.leftoverlink.service.UserService;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.leftoverlink.service.EmailService;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final EmailService emailService;

	public UserServiceImpl(UserRepository userRepository, EmailService emailService) {
	    this.userRepository = userRepository;
	    this.emailService = emailService;
	}

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    public void sendWelcome(User user) {
        try {
            emailService.sendWelcomeEmailWithInstructions(user);
        } catch (MessagingException e) {
            e.printStackTrace(); // or log.error("Email sending failed", e);
            // Optionally notify admin, retry, or mark failure status
        }
    }


}
