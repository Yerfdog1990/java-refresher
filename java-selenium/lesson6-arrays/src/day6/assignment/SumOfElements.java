package day6.assignment;

import java.util.Arrays;

public class SumOfElements {
    public static void main(String[] args) {
        int[]  array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println("Array: " + Arrays.toString(array));

        // Method 1: Using enhanced for-each loop
        int sum = 0;
        for (int element : array) {
            sum += element;
        }
        System.out.println("\nMethod 1: Using enhanced for-each loop:");
        System.out.println("Sum of elements : " + sum);

        // Method 2: Using Stream API
        int sumofElements = Arrays.stream(array).sum();
        System.out.println("\nMethod 2: Using Stream API:");
        System.out.println("Sum of elements : " + sumofElements);
    }
}
