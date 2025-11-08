package day5.assignment;

import java.util.Scanner;

public class ReverseNumber {
    public static void main(String[] args) {
        // Method 1: Using algorithm
        System.out.println("Method 1: Using algorithm:");
        System.out.print("Enter a number: ");
        Scanner input1 = new Scanner(System.in);
        int originalNumber1 = input1.nextInt();
        System.out.println("Original number: " + originalNumber1);
        int reversedNumber1 = 0;

        while (originalNumber1 != 0) {
            reversedNumber1 = reversedNumber1 * 10 + originalNumber1 % 10;
            originalNumber1 = originalNumber1 / 10;
        }
        System.out.println("Reversed number: " + reversedNumber1);

        // Method 2: Converting to string then reverse using StringBuffer class
        System.out.println("\nMethod 2: Using StringBuffer class:");
        System.out.print("Enter original number: ");
        Scanner input2 = new Scanner(System.in);
        int originalNumber2 = input2.nextInt();
        System.out.println("Original number: " + originalNumber2);
        StringBuilder reversedNumber2  = new StringBuilder(String.valueOf(originalNumber2));
        reversedNumber2.reverse();
        System.out.println("Reversed number: " + reversedNumber2);

        // Method 3: Converting to string then append character at "i" position using StringBuffer class
        System.out.println("\nMethod 3: Using StringBuilder class:");
        System.out.print("Enter original number: ");
        Scanner input3 = new Scanner(System.in);
        int originalNumber3 = input3.nextInt();
        System.out.println("Original number: " + originalNumber3);

        String strNumber = String.valueOf(originalNumber3); // Step 1: Convert the original number to String
        StringBuilder reversedString  = new StringBuilder(); // Step 2: Create a StringBuilder object

        for (int i = strNumber.length() -1; i >= 0 ; i--) { // Step 3: Iterate over the strNumber backwards
            reversedString.append(strNumber.charAt(i)); // Step 4: Append the character at "i" position to the String builder
        }
        System.out.println("Reversed number: " + reversedString); // Step 5: Print the reversed string
    }
}
