package day5.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CountOddAndEvenDigits {
    public static void main(String[] args) {
        System.out.print("Enter a  number: ");
        Scanner input = new Scanner(System.in);
        long number = input.nextLong();
        long temp = number;
        int evenNumbers = 0;
        int oddNumbers = 0;
        List<Long> evenNumbersList = new ArrayList<>();
        List<Long> oddNumbersList = new ArrayList<>();

        long rem;
        while (number > 0) {
            rem = number % 10;
            number = number / 10;
            if (rem % 2 == 0) {
                evenNumbersList.add(rem);
                evenNumbers++;
            } else {
                oddNumbersList.add(rem);
                oddNumbers++;
            }
        }
        System.out.println("There are " +evenNumbers+ " numbers in " + temp);
        System.out.println("There are " +oddNumbers+ " numbers  in " + temp);

        System.out.println("List of even numbers: " + evenNumbersList);
        System.out.println("List of odd numbers: " + oddNumbersList);
    }
}
