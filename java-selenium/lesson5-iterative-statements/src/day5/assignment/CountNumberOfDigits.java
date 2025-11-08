package day5.assignment;

import java.util.Scanner;

public class CountNumberOfDigits {
    public static void main(String[] args) {
        System.out.print("Enter a number: ");
        Scanner input = new Scanner(System.in);
        int number = input.nextInt();
         int count = 0;
          while (number > 0) {
              number /= 10;
              count++;
          }
        System.out.println("Number of digits: " + count);
    }
}
