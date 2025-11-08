package day6.assignment;

public class FindMissingNumber {
    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 6, 7, 8, 9, 10};
        int n = 10;
        int sumOfNValues = (n * (n + 1)) / 2;
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        int missingNumber = sumOfNValues - sum;
        System.out.println(missingNumber);
    }
}
