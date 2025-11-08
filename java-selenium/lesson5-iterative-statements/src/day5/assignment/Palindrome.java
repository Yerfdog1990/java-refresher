package day5.assignment;

import java.util.Scanner;


public class Palindrome {
    public static void main(String[] args) {
        // Palindrome number
        System.out.print("Enter a number: ");
        Scanner input1 = new Scanner(System.in);
        int originalNumber1 = input1.nextInt();
        int temp = originalNumber1; // Store original number in a temporary variable
        System.out.println("Original number: " + originalNumber1);
        int reversedNumber1 = 0;

        while (originalNumber1 != 0) {
            reversedNumber1 = reversedNumber1 * 10 + originalNumber1 % 10;
            originalNumber1 = originalNumber1 / 10;
        }
        System.out.println("Reversed number: " + reversedNumber1);
        if (temp == reversedNumber1) {
            System.out.println("The number is a palindrome");
        } else {
            System.out.println("The number is not a palindrome");
        }

        // Palindrome sting
        System.out.print("Enter a string: ");
        Scanner input2 = new Scanner(System.in);
        int originalNumber2 = input2.nextInt();
        System.out.println("Original number: " + originalNumber2);
        StringBuilder reversedNumber2  = new StringBuilder(String.valueOf(originalNumber2));
        reversedNumber2.reverse();
        System.out.println("Reversed number: " + reversedNumber2);
    }
}
