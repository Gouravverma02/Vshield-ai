package com.vshield.vshield.util;

import com.vshield.vshield.model.User;
import com.vshield.vshield.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public class SessionUserHelper {

    private SessionUserHelper() {
    }

    public static Optional<User> getSessionUser(HttpSession session, UserRepository userRepository) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }
}