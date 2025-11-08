package day9;

public class StringLiterals {
    public static void main(String[] args) {

        // 1.Encapsulation
        String language = "Java"; // String literal
        char letter = 'J';        // Char literal

        String name = "Alice";
        name.concat(" Smith"); // Creates a new String, not modifying 'name'
        System.out.println(name); // Output: Alice

        // 2.Immutability -> Strings in Java are immutable — modifying a string creates a new object.
        name = name.concat(" Smith");
        System.out.println(name); // Output: Alice Smith

        // 3.The String Pool (Memory Optimization)
        // Java stores string literals in a special memory area called the string pool.
        // If two identical string literals appear in the code, they both point to the same memory location.
        String s1 = "Java";
        String s2 = "Java";
        String s3 = new String("Java"); // Explicitly creates a new object

        System.out.println(s1 == s2); // true (same reference in string pool)
        System.out.println(s1 == s3); // false (different memory)
        System.out.println(s1.equals(s3)); // true (same content)

        // Use intern() method to add string to the pool
        /*
        The intern() method, when called on a string, performs the following actions:
            -It checks to see if the string pool already contains an identical string.
            -If an identical string is found, it returns the reference to that string from the pool.
            -If no identical string is found, it adds the string to the pool and returns a reference to this new, canonical version
         */

        String str1 = "CodeGym"; // This literal is automatically interned
        String str2 = new String("CodeGym"); // Creates a new object in the heap
        String str3 = s2.intern(); // s3 will now point to the interned string in the pool

        System.out.println(str1 == str2); // false, different memory locations
        System.out.println(str1 == str3); // true, both reference the same object in the pool
        System.out.println(str2 == str3); // false, different memory locations
        System.out.println(str1.equals(str2)); // true, checks for content equality
        System.out.println(str1.equals(str3)); // true, checks for content equality
    }
}
