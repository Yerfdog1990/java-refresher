package assignments;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveWhiteSpaces {
    public static void main(String[] args) {
        // Method 1: Using String replaceAll() methods
        String text = "F re d An  n a Al e xa nd er";
        System.out.println("Original string: " + text);

        // Replace all white spaces characters with empty string
        String result1 = text.replaceAll("\\s", "");
        System.out.println("Result: " + result1);

        // Method 2: Using Marcher replaceAll() method
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(text);
        String result2 = matcher.replaceAll("");  // This does the replacement in one go
        System.out.println("Result: " + result2);
    }
}
