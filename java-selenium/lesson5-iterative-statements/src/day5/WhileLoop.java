package day5;

public class WhileLoop {
    public static void main(String[] args) {
        System.out.println("Print consecutive numbers in a loop");
        int i = 0; // Initialization
        while (i <= 10) { // Condition
            System.out.println("i = " + i);
            i++; // Incrementation
        }
        System.out.println("\nPrint alternating odd and even numbers in a loop");
        int j = 0;
        while (j <= 10) {
            if(j % 2 == 0) {
                System.out.println(j + " is even");
            } else {
                System.out.println(j + " is odd");
            }
            j++;
        }
    }
}
