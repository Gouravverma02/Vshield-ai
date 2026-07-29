package com.vshield.vshield.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SECONDS = 15 * 60; // 15 minutes

    private final ConcurrentHashMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    private static class AttemptRecord {
        AtomicInteger count = new AtomicInteger(0);
        Instant windowStart = Instant.now();
    }

    public boolean isBlocked(String email) {
        AttemptRecord record = attempts.get(normalize(email));
        if (record == null) return false;

        if (Instant.now().isAfter(record.windowStart.plusSeconds(WINDOW_SECONDS))) {
            attempts.remove(normalize(email));
            return false;
        }
        return record.count.get() >= MAX_ATTEMPTS;
    }

    public void recordFailedAttempt(String email) {
        AttemptRecord record = attempts.computeIfAbsent(normalize(email), k -> new AttemptRecord());
        if (Instant.now().isAfter(record.windowStart.plusSeconds(WINDOW_SECONDS))) {
            record.count.set(0);
            record.windowStart = Instant.now();
        }
        record.count.incrementAndGet();
    }

    public void recordSuccess(String email) {
        attempts.remove(normalize(email));
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}