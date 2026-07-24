package com.vshield.vshield.util;

import java.util.List;

public class KeywordLists {

    public static final List<String> URGENCY_PHRASES = List.of(
            "act now",
            "act immediately",
            "urgent",
            "urgently",
            "immediately",
            "verify immediately",
            "verify your account now",
            "limited time",
            "expires today",
            "expires soon",
            "respond within",
            "final notice",
            "last chance",
            "don't miss out",
            "hurry",
            "time sensitive",
            "before it's too late"
    );

    public static final List<String> SHORTENED_LINK_DOMAINS = List.of(
            "bit.ly",
            "tinyurl.com",
            "t.co",
            "goo.gl",
            "ow.ly",
            "is.gd",
            "buff.ly",
            "rebrand.ly",
            "cutt.ly",
            "shorturl.at"
    );

    public static final List<String> KNOWN_BRANDS = List.of(
            "nike", "adidas", "sephora", "amazon", "netflix", "apple",
            "google", "microsoft", "instagram", "facebook", "tiktok",
            "youtube", "spotify", "starbucks", "coca-cola", "pepsi",
            "samsung", "sony", "disney", "puma"
    );

    public static final List<String> GENERIC_EMAIL_DOMAINS = List.of(
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
            "aol.com", "icloud.com", "protonmail.com", "mail.com"
    );

    public static final List<String> SENSITIVE_INFO_PHRASES = List.of(
            "password",
            "otp",
            "one-time code",
            "one time password",
            "verification code",
            "login details",
            "login credentials",
            "send payment",
            "wire transfer",
            "gift card",
            "credit card number",
            "cvv",
            "bank details",
            "social security",
            "confirm your identity by sending"
    );

    public static final List<String> GENERIC_GREETINGS = List.of(
            "dear creator",
            "dear user",
            "dear sir",
            "dear madam",
            "dear sir/madam",
            "dear valued customer",
            "dear account holder",
            "hello dear",
            "dear winner",
            "dear beneficiary"
    );

    private KeywordLists() {
        // Prevents instantiation - this class only holds static data
    }
}