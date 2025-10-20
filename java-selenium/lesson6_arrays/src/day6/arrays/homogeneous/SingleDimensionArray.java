package day6.arrays.homogeneous;

import java.util.Arrays;

public class SingleDimensionArray {
    public static void main(String[] args) {
        // Create a dynamic array
        int[] arr1 = {1,2,3,4,5,6,7,8,9,10};
        System.out.println("Dynamic array: " +Arrays.toString(arr1));
        // Create a fixed array
        int[] arr2 = new int[10];
        arr2[0] = 1;
        arr2[1] = 2;
        arr2[2] = 3;
        arr2[3] = 4;
        arr2[4] = 5;
        arr2[5] = 6;
        arr2[6] = 7;
        arr2[7] = 8;
        arr2[8] = 9;
        arr2[9] = 10;

        System.out.println("Fixed array: " +Arrays.toString(arr2));
    }
}
