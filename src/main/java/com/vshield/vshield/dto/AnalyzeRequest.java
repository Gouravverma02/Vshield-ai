package com.vshield.vshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AnalyzeRequest {

    @NotBlank(message = "Text is required")
    @Size(max = 5000, message = "Text must be under 5000 characters")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}