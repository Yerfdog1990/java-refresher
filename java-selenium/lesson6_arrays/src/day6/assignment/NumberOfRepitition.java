package day6.assignment;

public class NumberOfRepitition {
    public static void main (String[] args) {
        int[] numberArray = {1, 2, 3, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 6, 7, 8, 9, 10, 10, 10, 10};
        int number = 10;
        int count = 0;
        for (int value : numberArray) {
            if (value == number) {
                count++;
            }
        }
        System.out.println(number+ " is repeated " + count + " times.");
    }
}
