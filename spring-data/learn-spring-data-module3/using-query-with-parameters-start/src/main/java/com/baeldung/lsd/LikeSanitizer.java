package com.baeldung.lsd;

public class LikeSanitizer {

    /**
     * Escapes SQL LIKE wildcards: %, _, and \.
     */
    public static String escapeForLike(String input) {
        if (input == null) {
            return null;
        }

        return input
                .replace("\\", "\\\\")   // escape backslash first
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}

