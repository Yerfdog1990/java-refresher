package day8;

import java.util.Arrays;

public class MutableVsImmutable {
    public static void main(String[] args) {
        // Show that array is mutable
        int[] intArray = { 1, 2, 3, 4, 5 };
        System.out.println("Original array: " + Arrays.toString(intArray));
        Arrays.sort(intArray);
        System.out.println("Sorted array: " + Arrays.toString(intArray));

        // Show that string is immutable
        String str  = "Programming";
        System.out.println("Before concatenation: " + str); // Programming
        str.concat("languages");
        System.out.println("After concatenation: " + str); // Programming -> Original string unchanged after concatenation
    }
}
