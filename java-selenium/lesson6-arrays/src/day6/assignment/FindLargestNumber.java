package day6.assignment;

public class FindLargestNumber {
    public static void main(String[] args) {
        int[] numberArray = {1, 2, 3, 4, -5, 6, 7, 81, 9, 10, 11, 12, 13, 14, 15};
        int firstInder = numberArray[0];
        for (int number: numberArray) {
            if (number > firstInder) {
                firstInder = number;
            }
        }
        System.out.println("The largest number: " +firstInder);
    }
}
