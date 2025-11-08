package day5.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FindPrimeNumbers {
    public static void main(String[] args) {
        List<Integer> primeNumberList = new ArrayList<>();
        System.out.print("Enter number: ");
        Scanner scanner = new Scanner(System.in);
        int number;
        if (scanner.hasNextInt()) {
            number = scanner.nextInt();
            if (number <= 1){
                System.err.println("Number cannot be 1 or 0. Try again!!");
            }else{
                for (int numberToCheck = 2; numberToCheck <= number; numberToCheck++) {
                    boolean isPrime = true;
                    for (int factor = 2; factor <= numberToCheck/2; factor++) {
                        if (numberToCheck % factor == 0) {
                            isPrime = false;
                            break;
                        }
                    }
                    if (isPrime) {
                        primeNumberList.add(numberToCheck);
                    }
                }
                System.out.println("List of prime numbers in " +number+ " is: " +primeNumberList);
            }
        } else {
            System.err.println("Number must be an integer. Try again!!");
        }
    }
}
