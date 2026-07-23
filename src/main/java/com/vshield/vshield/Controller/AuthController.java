package com.vshield.vshield.Controller;

import com.vshield.vshield.dto.LoginRequest;
import com.vshield.vshield.dto.SignupRequest;
import com.vshield.vshield.model.User;
import com.vshield.vshield.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An account with this email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User(request.getEmail(), hashedPassword);
        userRepository.save(newUser);

        Map<String, Object> response = new HashMap<>();
        response.put("id", newUser.getId());
        response.put("email", newUser.getEmail());
        response.put("message", "Account created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty() ||
                !passwordEncoder.matches(request.getPassword(), userOptional.get().getPasswordHash())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User user = userOptional.get();
        session.setAttribute("userId", user.getId());
        session.setAttribute("userEmail", user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object userId = session.getAttribute("userId");
        Object userEmail = session.getAttribute("userEmail");

        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", userId);
        response.put("email", userEmail);
        return ResponseEntity.ok(response);
    }
}