package day6.assignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OddAndEvenElements {
    public static void main(String[] args) {
        int[] arr = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        System.out.println("Original array: " + Arrays.toString(arr));

        // Method 1: Using enhanced for-each loop
        List<Integer> even = new ArrayList<>();
        List<Integer> odd = new ArrayList<>();

        for (int element : arr) {
            if (element % 2 == 0) {
                even.add(element);
            } else {
               odd.add(element);
            }
        }
        System.out.println("\nMethod 1: Using enhanced for-each loop");
        System.out.println("Even numbers: " +even);
        System.out.println("Odd numbers: " +odd);

        // Method 2: Using Stream API
        List<Integer> evenNumbers = Arrays.stream(arr)
                .filter(n -> n % 2 == 0)
                .boxed()
                .toList();
        List<Integer> oddNumbers = Arrays.stream(arr)
                .filter(n -> n % 2 != 0)
                .boxed()
                .toList();

        System.out.println("\nMethod 2: Using Stream API");
        System.out.println("Even numbers: " +evenNumbers);
        System.out.println("Odd numbers: " +oddNumbers);
    }
}
