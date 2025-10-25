package assignments;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveSpecialCharacters {
    public static void main(String[] args) {
        // Method 1: Using String replaceAll() methods
        String text = "Fred An*na Alexa%nder";
        System.out.println("Original string: " + text);

        // Replace all non-alphabetic characters with empty string
        String result1 = text.replaceAll("[^A-Za-z]", "");
        System.out.println("Result: " + result1);

        // Method 2: Using Marcher replaceAll() method
        Pattern pattern = Pattern.compile("[^A-Za-z]");
        Matcher matcher = pattern.matcher(text);
        String result2 = matcher.replaceAll("");  // This does the replacement in one go
        System.out.println("Result: " + result2);
    }
}
