package day9;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherClass {
    public static void main(String[] args) {
        // public Matcher matcher(CharSequence input)
        Pattern p = Pattern.compile("a*b"); // Create a compiled representation of the regular expression
        Matcher m = p.matcher("aaaaab"); // Create a "search engine" to search the text "aaaaab" for the pattern "a*b"
        System.out.println(m.matches());

        String text = "Fred Anna Alexa";
        Pattern pattern = Pattern.compile("A.+?a");

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start=matcher.start();
            int end=matcher.end();
            System.out.println("Match found: " + text.substring(start, end) + " from index "+ start + " through " + (end-1));
        }
        System.out.println(matcher.replaceFirst("Ira"));
        System.out.println(matcher.replaceAll("Mary"));
        System.out.println(text);
    }
}
