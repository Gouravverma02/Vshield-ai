package com.vshield.vshield.model;

public class CheckResult {

    private final boolean triggered;
    private final String reason;
    private final int weight;

    public CheckResult(boolean triggered, String reason, int weight) {
        this.triggered = triggered;
        this.reason = reason;
        this.weight = weight;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public String getReason() {
        return reason;
    }

    public int getWeight() {
        return weight;
    }
}