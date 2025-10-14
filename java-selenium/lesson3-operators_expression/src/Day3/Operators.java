package Day3;

public class Operators {
    public static void main(String[] args) {
        // Arithmetic operators
        int a = 10, b = 3;

        System.out.println("Sum: " + (a + b));
        System.out.println("Difference: " + (a - b));
        System.out.println("Product: " + (a * b));
        System.out.println("Division: " + (a / b));
        System.out.println("Remainder: " + (a % b));

        // Relational (Comparison) Operators
        System.out.println(a < b);   // true
        System.out.println(a == b);  // false
        System.out.println(a != b);  // true

        // Logical Operators
        boolean x = true;
        boolean y = false;

        System.out.println(x && y);  // false
        System.out.println(x || y);  // true
        System.out.println(!x);      // false

        // Increment and Decrement Operators
        int m = 5;
        System.out.println(m++); // 5 (post-increment)
        System.out.println(m);   // 6

        int n = 5;
        System.out.println(++n); // 6 (pre-increment)

        int k = 5;
        System.out.println(k--); // 5 (de-increment)
        System.out.println(k);   // 4

        int l = 5;
        System.out.println(--l); // 4 (pre-decrement)

        // Assignment Operators
        int z = 10;
        z += 5; // z = z + 5
        System.out.println(z); // 15


        // Conditional (Ternary) Operator
        int age = 20;
        String status = (age >= 18) ? "Adult" : "Minor";
        System.out.println(status); // Adult
    }
}
