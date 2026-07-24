package com.vshield.vshield.model;

import java.util.List;

public class AnalysisResult {

    private final int riskScore;
    private final Verdict verdict;
    private final List<String> reasons;
    private final List<String> nextSteps;

    public AnalysisResult(int riskScore, Verdict verdict, List<String> reasons, List<String> nextSteps) {
        this.riskScore = riskScore;
        this.verdict = verdict;
        this.reasons = reasons;
        this.nextSteps = nextSteps;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public List<String> getNextSteps() {
        return nextSteps;
    }
}