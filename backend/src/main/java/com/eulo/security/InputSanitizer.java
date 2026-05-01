package com.eulo.security;

/**
 * Sanitizes free-text input to prevent XSS and injection attacks.
 * Applied to all user-supplied text fields before persistence.
 */
public final class InputSanitizer {

    private InputSanitizer() {}

    /**
     * Strips HTML tags, removes control characters, and trims whitespace.
     * Use for general text fields (names, descriptions, notes, bios).
     */
    public static String sanitize(String input) {
        if (input == null) return null;

        // Remove HTML tags (prevents stored XSS)
        String result = input.replaceAll("<[^>]*>", "");

        // Remove null bytes and control characters (prevents injection attacks)
        result = result.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");

        // Remove potential JS protocol handlers left after tag stripping
        result = result.replaceAll("(?i)(javascript|vbscript|data)\\s*:", "");

        return result.trim();
    }
}
