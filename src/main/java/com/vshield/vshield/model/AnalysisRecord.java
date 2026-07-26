package com.vshield.vshield.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_records")
public class AnalysisRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String verdict;

    @Column(nullable = false)
    private int riskScore;

    @Lob
    @Column(nullable = false)
    private String originalText;

    @Lob
    @Column(nullable = false)
    private String reasons;

    @Lob
    @Column(nullable = false)
    private String nextSteps;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AnalysisRecord() {
    }

    public AnalysisRecord(User user, String verdict, int riskScore, String originalText,
                          String reasons, String nextSteps) {
        this.user = user;
        this.verdict = verdict;
        this.riskScore = riskScore;
        this.originalText = originalText;
        this.reasons = reasons;
        this.nextSteps = nextSteps;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getVerdict() {
        return verdict;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getReasons() {
        return reasons;
    }

    public String getNextSteps() {
        return nextSteps;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}