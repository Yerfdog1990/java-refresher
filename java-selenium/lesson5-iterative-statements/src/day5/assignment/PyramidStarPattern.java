package day5.assignment;

public class PyramidStarPattern {
    public static void main(String[] args) {
        int row = 6;
        for (int i = 0; i < row; i++) {
            // Print leading spaces
            for (int j = 0; j < row - i - 1; j++) {
                System.out.print(" ");
            }
            // Print stars
            for (int j = 0; j <= i; j++) {
                System.out.print("* ");
            }
            // Move to the next line after each row
            System.out.println();
        }
    }
}
