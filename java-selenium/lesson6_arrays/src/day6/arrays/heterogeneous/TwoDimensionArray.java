package day6.arrays.heterogeneous;

import java.util.Arrays;

public class TwoDimensionArray {
    public static void main(String[] args) {
        Object [][]arr = {{"Cat",2.7,3, 12L, 'M'},{4,"8",6.8, 10L, 'F'}};
        System.out.println("Two dimensions array: " +Arrays.deepToString(arr));
    }
}
