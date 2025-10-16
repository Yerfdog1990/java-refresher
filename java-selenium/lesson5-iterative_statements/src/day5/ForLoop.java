package day5;

public class ForLoop {
    public static void main(String[] args) {
        System.out.println("\nPrint all numbers in a loop");
        // Print all numbers in a loop
        for (int i = 0; i < 10; i++) {
            System.out.print(i +" ,");
        }

        System.out.println("\nPrint even numbers in a loop");
        // Print only even numbers in a loop
        for (int i = 0; i < 10; i++) {
            if ( i % 2 == 0) {
                System.out.print(i +" ,");
            }
        }

        System.out.println("\nPrint odd numbers in a loop");
        // Print only even numbers in a loop
        for (int i = 0; i < 10; i++) {
            if ( i % 2 != 0) {
                System.out.print(i +" ,");
            }
        }
    }
}
