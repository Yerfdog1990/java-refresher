package day9;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Quantifiers {
    public static void main(String[] args) {
        String text = "Fred Anna Alexander";
        System.out.println("Original string: " + text);
        greedyQuantifier(text);
        possessiveQuantifier(text);
        reluctantQuantifier(text);
    }

    /*
   Greedy Quantifier (A.+a):
       - "." matches any character
       - "+" is greedy, so it matches as many characters as possible
       - Then backtracks to find the last a
       - Matches "Anna Alexa" in "Fred Anna Alexander"
    */
    private static void greedyQuantifier(String text) {
        Pattern pattern = Pattern.compile("A.+a");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            System.out.println("Greedy quantifier: " +text.substring(matcher.start(), matcher.end()));
        }
    }

    /*
    Possessive Quantifier (A.++a):
        - "." matches any character
        - "++" is possessive and doesn't allow backtracking
        - It matches as many characters as possible and doesn't give them up
        - The "a" at the end can't match because all characters after the first "A" are consumed by ".+"
        - Result: No match found
     */
    private static void possessiveQuantifier(String text) {
        Pattern pattern = Pattern.compile("A.++a");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            System.out.println("Possessive quantifier: " +text.substring(matcher.start(), matcher.end()));
        }
    }

    /*
    Reluctant Quantifier (A.+?a):
        - "+?" is reluctant, matching as few characters as possible
        - It finds the shortest possible match
        - Matches "Anna" and "Alexa" separately
     */
    private static void reluctantQuantifier(String text) {
        Pattern pattern = Pattern.compile("A.+?a");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            System.out.println("Reluctant quantifier: " +text.substring(matcher.start(), matcher.end()));
        }
    }
}
