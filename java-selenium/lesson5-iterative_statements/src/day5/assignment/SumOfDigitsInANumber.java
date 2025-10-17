package day5.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SumOfDigitsInANumber {
    public static void main(String[] args) {
        System.out.print("Enter a  number: ");
        Scanner input = new Scanner(System.in);
        long number = input.nextLong();

        long temp  = number;

        List<Long> numbersList = new ArrayList<>();

        int sum = 0;
        long remainder;
        while (number> 0) {
            remainder = number % 10;
            numbersList.add(remainder);
            number = number / 10;
        }
        for (int i = 0; i < numbersList.size(); i++) {
            sum += numbersList.get(i);
        }
        System.out.println("Sum of digits in " +temp+ " is: " +sum);
    }
}
