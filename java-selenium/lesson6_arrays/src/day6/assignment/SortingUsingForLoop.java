package day6.assignment;

import java.util.Arrays;

public class SortingUsingForLoop {
    public static void main(String[] args) {
        int[] numberArray = {2, 5, 7, 1, 20, 4, 3};
        System.out.println("Before sorting: "  + Arrays.toString(numberArray));

        for (int i = 0; i < numberArray.length; i++) {
            for (int j = i + 1; j < numberArray.length; j++) {
                if (numberArray[i] > numberArray[j]) {
                    int temp = numberArray[i];
                    numberArray[i] = numberArray[j];
                    numberArray[j] = temp;
                }
            }
        }

        System.out.println("After sorting: "  + Arrays.toString(numberArray));
    }
}
