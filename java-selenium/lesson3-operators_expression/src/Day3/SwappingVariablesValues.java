package Day3;

public class SwappingVariablesValues {
    public static void main(String[] args) {
        // Method 1: Using a Third (Temporary) Variable — The Standard Method
        int a = 7, b = 10;

        System.out.println("Before Swap: a = " + a + ", b = " + b);

        // Step 1: Store value of 'a' in 'temp'
        int temp = a;   // temp = 10

        // Step 2: Copy 'b' into 'a'
        a = b;          // a = 20

        // Step 3: Copy original 'a' (from temp) into 'b'
        b = temp;       // b = 10

        System.out.println("After Swap: a = " + a + ", b = " + b);

        // Method 2: Swapping Without a Third Variable — Arithmetic Swap
        int x = 5, y = 8;

        System.out.println("Before Swap: x = " + x + ", y = " + y);

        // Step 1: Add x and y
        x = x + y;  // x = 13

        // Step 2: Subtract new y from x
        y = x - y;  // y = 5 (original x)

        // Step 3: Subtract new y from x
        x = x - y;  // x = 8 (original y)

        System.out.println("After Swap: x = " + x + ", y = " + y);
    }
}
