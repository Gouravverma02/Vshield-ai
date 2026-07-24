package com.vshield.vshield;

import com.vshield.vshield.model.AnalysisResult;
import com.vshield.vshield.service.DetectionService;
import org.junit.jupiter.api.Test;

public class DetectionServiceTest {

    private final DetectionService detectionService = new DetectionService();

    @Test
    void runSampleTexts() {
        String[] samples = {
                "Hi! We are Nike and want to offer you a deal. Act now, verify your account immediately at nike-brand-deals.bit.ly and send your login password to confirm.",

                "Dear Creator, CONGRATULATIONS!!! You have been selected for a limited time offer! Respond within 24 hours or lose this opportunity! Send your bank details now!!!",

                "Hi Sarah, this is Mark from Acme Marketing Co. We'd love to collaborate with you on a sponsored post for our new product line. Let me know if you're interested and we can discuss rates and timeline.",

                "Hello! I'm reaching out from Adidas (adidas-partnerships@gmail.com) about a potential brand partnership. Please verify your account immediately by sending your password to secure this urgent offer.",

                "Hey! Loved your last video. I run a small skincare brand and would love to send you some products to try, no strings attached. Totally understand if it's not your thing!",

                "Dear Sir/Madam, this is an urgent notice regarding your account. Click here: bit.ly/xyz123 to verify your identity immediately or your account will be suspended within 24 hours!",

                "Hi, this is Priya from Sephora India (priya.sharma@sephora.in). We're launching a new campaign and think you'd be a great fit. Happy to hop on a call this week to discuss details.",

                "URGENT!!! Your PayPal account has been LIMITED! Verify now by sending your card number and CVV to avoid permanent suspension! Act immediately!!!"
        };

        for (int i = 0; i < samples.length; i++) {
            AnalysisResult result = detectionService.analyze(samples[i]);
            System.out.println("=== Sample " + (i + 1) + " ===");
            System.out.println("Text: " + samples[i].substring(0, Math.min(60, samples[i].length())) + "...");
            System.out.println("Risk Score: " + result.getRiskScore());
            System.out.println("Verdict: " + result.getVerdict());
            System.out.println("Reasons:");
            for (String reason : result.getReasons()) {
                System.out.println("  - " + reason);
            }
            System.out.println("Next Steps:");
            for (String step : result.getNextSteps()) {
                System.out.println("  - " + step);
            }
            System.out.println();
        }
    }
}