package day6.string;

import java.util.Arrays;

public class StringMethods {
    public static void main(String[] args) {
        // 1.Creating a String variable
        String str1 = "Hello";
        System.out.println(str1);

        String str2 = new String("World");
        System.out.println(str2);

        // 2.String concatenation -> Joining strings
        String s1 = "Hello";
        String s2 = " Java";
        String s3 = " Programming";

        // 2.1: Join two strings
        System.out.println(s1 + s2); // Using addition operator
        System.out.println(s1.concat(s2)); // using conct() method

        // 2.2: Join three strings
        System.out.println(s1 + s2 + s3); // Using addition operator
        System.out.println(s1.concat(s2).concat(s3)); // using conct() method

        // 2.3: Join strings using their values
        // 2.1: Joint two strings
        System.out.println("String 1" + " String 2"); // Using addition operator
        System.out.println("String 1".concat(" String 2")); // using conct() method

        // 3.Length of a string
        System.out.println(s1.length());
        System.out.println("String 1".length());

        // 4.trim() -> Remove spaces to the right and left of the string
        String s = "    Java      ";
        System.out.println("Before trimming: " +s.length());
        System.out.println("After trimming: " +s.trim().length());

        // 5.charAt() -> Retrieve character from a string based on its index.
        String c = "Programming";
        System.out.println("First character: " +c.charAt(0));

        // 6.contains() -> Check if part of a string or character belongs to a string
        String d = "Programming";
        String e = "grammi";
        String f = "PROG";
        System.out.println("Is part of the string? " + d.contains(e)); // true
        System.out.println("Is part of the string? " + d.contains(f)); // false
        System.out.println("Is part of the string? " + d.toLowerCase().contains(f.toLowerCase())); // true
        System.out.println("Is part of the string? " + d.replace(d, d.toUpperCase()).contains(f)); // true

        // 7.equals() -> Compare if two strings are equal (case-sensitive)
        String g = "Programming";
        String h = "Programming";
        String i = "ProgrammIng";
        System.out.println("Both are equal? " + h.equals(g)); // true
        System.out.println("Both are equal? " + h.equals(i)); // false

        // 8.equalsIgnoreCase)
        System.out.println("Both are equal? " + h.equalsIgnoreCase(g)); // true
        System.out.println("Both are equal? " + h.equalsIgnoreCase(i)); // true

        // replace() -> Replace single or a sequence of characters in a string
        String j = "Hello Java Programming";
        System.out.println("Before replacement: " +j);
        System.out.println("After replacement: " +j.replace("Java","Python"));
        System.out.println("After replacement: " +j.replace("Java","Python").replace("Programming","Programmer"));

        String amount1 = "$12,540,678";
        String amount2 = "$10,040,975";
        System.out.println("Before replacement: " +amount1);
        String convertedAmount1 = amount1.replace("$", "").replace(",", "");
        System.out.println("After replacement: " + convertedAmount1);
        //System.out.println("Before replacement: " +amount2);
        String convertedAmount2 = amount2.replace("$", "").replace(",", "");
        //System.out.println("After replacement: " + convertedAmount2);
        System.out.println(amount1+ " + " + amount2 + " = $" + (Integer.parseInt(convertedAmount1) + Integer.parseInt(convertedAmount2)));


        // substring() -> Extract sub-string from the main string
        String k = "Programming";
        System.out.println("Before replacement: " +k);
        System.out.println("After replacement: " +k.substring(0,k.length()-1)); // return "Programmin"
        System.out.println("After replacement: " +k.substring(0,4)); // return "Prog"

        // toLower() and toUpper()
        String l = "Programming";
        System.out.println("Original string: " +l);
        System.out.println("Lower case: " +l.toLowerCase());
        System.out.println("Upper case: " +l.toUpperCase());

        // split() -> split the string into multiple parts based on the delimiter
        // Restricted delimiters: -, *, &, %, ( ), ^
        String m = "joe@example.com";
        System.out.println("Before splitting: " +m);
        String[] words = m.split("@");
        System.out.println("After splitting: " + Arrays.toString(words)); // [joe, example.com]
        System.out.println("String before \"@\": " + Arrays.toString(words[0].split("@"))); // [joe]
        System.out.println("String before \"@\": " + words[0]); // [joe]
        System.out.println("String after \"@\": " + Arrays.toString(words[1].split("@"))); // [example.com]
        System.out.println("String after \"@\": " + words[1]); // [example.com]


    }
}
