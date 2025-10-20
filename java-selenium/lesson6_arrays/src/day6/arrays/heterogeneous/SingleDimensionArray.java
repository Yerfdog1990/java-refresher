package day6.arrays.heterogeneous;

import java.util.Arrays;

public class SingleDimensionArray {
    public static void main(String[] args) {
        // Create a dynamic array
        Object[] arr1 = {1,"Boy",'A',4.0,5.2f,6L};
        System.out.println("Dynamic array: " +Arrays.toString(arr1));
        // Create a fixed array
        Object[] arr2 = new Object[6];
        arr2[0] = 1;
        arr2[1] = "Kenya";
        arr2[2] = 'C';
        arr2[3] = 4.9;
        arr2[4] = 4.2f;
        arr2[5] = 6L;

        System.out.println("Fixed array: " +Arrays.toString(arr2));
    }
}
