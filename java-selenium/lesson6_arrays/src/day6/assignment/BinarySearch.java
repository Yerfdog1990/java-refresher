package day6.assignment;

import java.util.Scanner;

public class BinarySearch {
    public static void main(String[] args) {
        int[] numberArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        System.out.print("Enter number to search: ");
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextInt()) {
            int target = scanner.nextInt();
            int result = binarySearch(numberArray, target);
            if (result != -1) {
                System.out.println("The given number " + target + " found at index " + result);
            } else {
                System.out.println("Number " + target + " not found");
            }
        } else {
            System.err.println("Number must be an integer. Try again!!");
        }
        scanner.close();
    }

    public static int binarySearch(int[] arr, int target) {
        int leftIndex = 0;
        int rightIndex = arr.length - 1;

        while (leftIndex <= rightIndex) {
            int mid = leftIndex + (rightIndex - leftIndex) / 2;

            // Check if target is present at mid
            if (arr[mid] == target) {
                return mid;
            }

            // If target greater, ignore left half
            if (arr[mid] < target) {
                leftIndex = mid + 1;
            }
            // If target is smaller, ignore right half
            else {
                rightIndex = mid - 1;
            }
        }
        // Target was not found in the array
        return -1;
    }
}