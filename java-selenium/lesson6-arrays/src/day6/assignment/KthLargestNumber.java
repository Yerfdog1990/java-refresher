package day6.assignment;

public class KthLargestNumber {
    public static void main(String[] args) {
        int[] numberArray = {3, 2, 8, 7, 12, 6, 10, 9, 5, 4, 1};
        int k = 3;
        int[] largest = new int[k];

        // Initialize the largest array with minimum possible value
        for (int i = 0; i < k; i++) {
            largest[i] = Integer.MIN_VALUE;
        }

        // Find k largest numbers
        for (int num : numberArray) {
            for (int i = 0; i < k; i++) {
                if (num > largest[i]) {
                    // Shift elements to make room for the new larger number
                    for (int j = k - 1; j > i; j--) {
                        largest[j] = largest[j - 1];
                    }
                    largest[i] = num;
                    break;
                }
            }
        }

        System.out.println("The " + k + "rd largest number is: " + largest[k-1]);
    }
}