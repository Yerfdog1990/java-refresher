package day6.assignment;

import java.util.Scanner;

public class LinearSearch {
    public static void main(String[] args) {
        int[] numberArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        System.out.print("Enter number to search: ");
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextInt()) {
            int number = scanner.nextInt();

            linearSearch(numberArray, number);
        }else{
            System.err.println("Number must be an integer. Try again!!");
        }
        scanner.close();
    }
    public static void linearSearch(int[] numberArray, int number) {
        boolean found = false;
        for (int value : numberArray) {
            if (value == number) {
                System.out.println("The given number " + number + " found");
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Number " +number + " not found");
        }
    }
}
