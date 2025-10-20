package day5.assignment;

import java.util.Scanner;

public class CheckIfPrimeNumber {

    public static void main(String[] args) {
       // Try changing this to test different numbers
        System.out.print("Enter number: ");
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        System.out.println("Is " + number + " a prime number? " + isPrimeDetailed(number));
    }

    public static boolean isPrimeDetailed(int number) {
        if (number <= 1) {
            System.out.println(number + " is NOT prime (less than or equal to 1).");
            return false;
        }

        System.out.println("Testing factors for number: " + number);

        // Test each factor from 2 up to number / 2
        for (int factor = 2; factor <= number / 2; factor++) {
            System.out.println("→ Trying factor: " + factor);

            if (number % factor == 0) {
                System.out.println("❌ Divides evenly by " + factor + " → NOT PRIME");
                return false; // stop early, not prime
            } else {
                System.out.println("✅ " + number + " % " + factor + " = " + (number % factor) + " → not divisible");
            }
        }

        System.out.println("✅ No factors found → PRIME");
        return true;
    }
}
