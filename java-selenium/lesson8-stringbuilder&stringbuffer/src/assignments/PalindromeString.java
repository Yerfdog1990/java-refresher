package assignments;

import java.util.Scanner;

public class PalindromeString {
    public static void main(String[] args) {
        System.out.print("Enter a string: ");
        try (Scanner scanner = new Scanner(System.in)) {
            String input = scanner.nextLine();
            StringBuilder builder = new StringBuilder();
            for (int i = input.length() - 1; i >= 0; i--) {
                builder.append(input.charAt(i));
            }
            System.out.println("Reversed string: " +builder);
            System.out.println("\"" +input+ "\"" +" is a palindrome? "+ input.contentEquals(builder));
        }
    }
}
