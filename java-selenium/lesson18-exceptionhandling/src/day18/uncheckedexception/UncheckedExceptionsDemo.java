package day18.uncheckedexception;

import java.util.Scanner;

public class UncheckedExceptionsDemo {
    public static void main(String[] args) {
        System.out.println("Program started .....");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a number: ");
        int number = scanner.nextInt();
        try {
            int result = 100 / number;
            System.out.println("Result: " + result);
        } catch (ArithmeticException e) {
            System.out.println("Cannot divide by zero");
        } finally {
            System.out.println("Program ended .....");
        }
    }
}
