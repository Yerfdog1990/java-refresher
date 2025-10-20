package day6.assignment;

import java.util.ArrayList;
import java.util.List;

public class PrimeNumbers {
    public static void main(String[] args) {
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        List<Integer> primeNumbers1 = new ArrayList<>();

        for (int number : array) {
            if (isPrime1(number)) {
                primeNumbers1.add(number);
            }
        }
        System.out.println("prime numbers: " + primeNumbers1);

        List<Integer> primeNumbers2 = new ArrayList<>();
        for (int number : array) {
            if (isPrime2(number)) {
                primeNumbers2.add(number);
            }
        }
        System.out.println("prime numbers: " + primeNumbers2);

        List<Integer> primeNumbers3 = new ArrayList<>();
        for (int number : array) {
            if (isPrime3(number)) {
                primeNumbers3.add(number);
            }
        }
        System.out.println("prime numbers: " + primeNumbers3);
    }

    public static boolean isPrime1(int number) {
        if (number <= 1) return false; // 1 and 0 are not prime numbers
        for (int factor = 2; factor <= number / 2; factor++) {
            System.out.println(factor+ " is a factor of " + number);
            if (number % factor == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPrime2(int number) {
        int count = 0;
        if (number <= 1) return false; // 1 and 0 are not prime numbers
        for (int factor = 1; factor <= number; factor++) {
            if (number % factor == 0)
                count++;
        }
        return count == 2;
    }

    public static boolean isPrime3(int number) {
        if (number <= 1) return false; // 1 and 0 are not prime numbers
        for (int factor = 2; factor <= Math.sqrt(number); factor++) {
            if (number % factor == 0) {
                return false;
            }
        }
        return true;
    }
}