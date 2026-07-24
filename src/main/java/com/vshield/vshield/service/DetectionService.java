package com.vshield.vshield.service;

import com.vshield.vshield.model.AnalysisResult;
import com.vshield.vshield.model.CheckResult;
import com.vshield.vshield.model.Verdict;
import com.vshield.vshield.util.KeywordLists;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DetectionService {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(/[\\w\\-./?%&=]*)?"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    );

    public AnalysisResult analyze(String text) {
        String lowerText = text == null ? "" : text.toLowerCase();

        List<CheckResult> checks = new ArrayList<>();
        checks.add(checkUrgencyLanguage(lowerText));
        checks.add(checkSuspiciousLinks(lowerText));
        checks.add(checkSenderMismatch(lowerText));
        checks.add(checkSensitiveInfoRequest(lowerText));
        checks.add(checkGenericTone(lowerText));

        int totalScore = 0;
        List<String> reasons = new ArrayList<>();

        for (CheckResult check : checks) {
            if (check.isTriggered()) {
                totalScore += check.getWeight();
                reasons.add(check.getReason());
            }
        }

        int riskScore = Math.min(totalScore, 100);
        Verdict verdict = mapScoreToVerdict(riskScore);
        List<String> nextSteps = buildNextSteps(verdict, checks);

        return new AnalysisResult(riskScore, verdict, reasons, nextSteps);
    }

    private CheckResult checkUrgencyLanguage(String lowerText) {
        for (String phrase : KeywordLists.URGENCY_PHRASES) {
            if (lowerText.contains(phrase)) {
                return new CheckResult(
                        true,
                        "Urgent/pressure language detected: '" + phrase + "'",
                        20
                );
            }
        }
        return new CheckResult(false, null, 0);
    }

    private CheckResult checkSuspiciousLinks(String lowerText) {
        Matcher matcher = URL_PATTERN.matcher(lowerText);
        while (matcher.find()) {
            String url = matcher.group();
            for (String shortener : KeywordLists.SHORTENED_LINK_DOMAINS) {
                if (url.contains(shortener)) {
                    return new CheckResult(
                            true,
                            "Suspicious shortened link detected (" + shortener + ")",
                            25
                    );
                }
            }
        }
        return new CheckResult(false, null, 0);
    }

    private CheckResult checkSenderMismatch(String lowerText) {
        String mentionedBrand = null;
        for (String brand : KeywordLists.KNOWN_BRANDS) {
            if (lowerText.contains(brand)) {
                mentionedBrand = brand;
                break;
            }
        }

        if (mentionedBrand == null) {
            return new CheckResult(false, null, 0);
        }

        Matcher emailMatcher = EMAIL_PATTERN.matcher(lowerText);
        if (emailMatcher.find()) {
            String email = emailMatcher.group();
            for (String genericDomain : KeywordLists.GENERIC_EMAIL_DOMAINS) {
                if (email.endsWith(genericDomain)) {
                    return new CheckResult(
                            true,
                            "Sender claims to be '" + mentionedBrand + "' but uses a generic email domain (" + genericDomain + ")",
                            25
                    );
                }
            }
            if (!email.contains(mentionedBrand)) {
                return new CheckResult(
                        true,
                        "Sender claims to be '" + mentionedBrand + "' but the email domain does not match",
                        20
                );
            }
        }
        return new CheckResult(false, null, 0);
    }

    private CheckResult checkSensitiveInfoRequest(String lowerText) {
        for (String phrase : KeywordLists.SENSITIVE_INFO_PHRASES) {
            if (lowerText.contains(phrase)) {
                return new CheckResult(
                        true,
                        "Message requests sensitive information: '" + phrase + "'",
                        35
                );
            }
        }
        return new CheckResult(false, null, 0);
    }

    private CheckResult checkGenericTone(String lowerText) {
        for (String greeting : KeywordLists.GENERIC_GREETINGS) {
            if (lowerText.contains(greeting)) {
                return new CheckResult(
                        true,
                        "Generic, mass-sent-style greeting detected: '" + greeting + "'",
                        15
                );
            }
        }

        long exclamationCount = lowerText.chars().filter(c -> c == '!').count();
        if (exclamationCount >= 3) {
            return new CheckResult(
                    true,
                    "Excessive exclamation marks detected - common in mass-sent scam messages",
                    10
            );
        }

        return new CheckResult(false, null, 0);
    }

    private Verdict mapScoreToVerdict(int score) {
        if (score <= 30) {
            return Verdict.SAFE;
        } else if (score <= 65) {
            return Verdict.SUSPICIOUS;
        } else {
            return Verdict.DANGEROUS;
        }
    }

    private List<String> buildNextSteps(Verdict verdict, List<CheckResult> checks) {
        List<String> steps = new ArrayList<>();

        boolean sensitiveInfoTriggered = checks.get(3).isTriggered();
        boolean linkTriggered = checks.get(1).isTriggered();
        boolean senderMismatchTriggered = checks.get(2).isTriggered();

        if (verdict == Verdict.SAFE) {
            steps.add("No major red flags detected - still use your best judgment");
            steps.add("Verify unfamiliar senders through official channels if unsure");
            return steps;
        }

        if (linkTriggered) {
            steps.add("Do not click the link provided");
        }
        if (sensitiveInfoTriggered) {
            steps.add("Never share passwords, OTPs, or payment details via message");
        }
        if (senderMismatchTriggered) {
            steps.add("Verify this offer directly through the brand's official/verified channels");
        }

        if (verdict == Verdict.DANGEROUS) {
            steps.add("Consider blocking and reporting this sender");
        } else {
            steps.add("Proceed with caution and verify before responding");
        }

        return steps;
    }
}