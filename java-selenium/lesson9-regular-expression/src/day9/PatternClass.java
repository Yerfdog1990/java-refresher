package day9;

import java.util.regex.Pattern;

public class PatternClass {
    public static void main(String[] args) {
        // String pattern() ‒> returns the regular expression's original string representation used to create the Pattern object:
        Pattern pattern1 = Pattern.compile("abc");
        System.out.println("String pattern: " +pattern1.pattern()); // "abc"

        // static boolean matches(String regex, CharSequence input) – lets you check the regular expression passed as regex against the text passed as input.
        // Returns:
        // -> true – if the text matches the pattern;
        // -> false – if it does not;
        System.out.println(Pattern.matches("A.+a","Anna")); // true
        System.out.println(Pattern.matches("A.+a","Fred Anna Alexander")); // false

        // int flags() ‒ returns the value of the pattern's flags parameter set when the pattern was created or 0 if the parameter was not set.
        // For example:
        Pattern pattern2 = Pattern.compile("abc");
        System.out.println("Number of flags: " +pattern2.flags()); // 0
        Pattern pattern3 = Pattern.compile("abc",Pattern.CASE_INSENSITIVE);
        System.out.println("Number of flags: " +pattern3.flags()); // 2

        // String[] split(CharSequence text, int limit) – splits the passed text into a String array.
        // The limit parameter indicates the maximum number of matches searched for in the text:
        String text = "Fred Anna Alexa";
        Pattern pattern4 = Pattern.compile("\\s");
        String[] strings = pattern4.split(text,2);
        for (String s : strings) {
            System.out.println(s);
        }
        System.out.println("---------");
        String[] strings1 = pattern4.split(text);
        for (String s : strings1) {
            System.out.println(s);
        }
    }
}
