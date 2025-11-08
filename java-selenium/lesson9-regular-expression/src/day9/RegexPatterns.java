package day9;

import java.util.regex.Pattern;

public class RegexPatterns {
    public static void main(String[] args) {
        // Simple Regex Patterns in Java — Practical Examples

        // 1. Matching Letters Only
        String letterPattern = "^[A-Za-z]+$"; // Only letters, at least 1 character long
        String testString1 = "HelloWorld";

        boolean matches = Pattern.matches(letterPattern, testString1);
        System.out.println("Is letters only? " + matches);

        // 2. Matching Digits Only
        String digitPattern = "^[0-9]+$";
        String testString = "12345";

        matches = Pattern.matches(digitPattern, testString);
        System.out.println("Is digits only? " + matches);

        // 3. Matching an Email Format
        // Basic pattern for demonstration, not covering all cases
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        String testEmail = "hello@example.com";

        matches = Pattern.matches(emailPattern, testEmail);
        System.out.println("Is valid email format? " + matches);
    }
}
