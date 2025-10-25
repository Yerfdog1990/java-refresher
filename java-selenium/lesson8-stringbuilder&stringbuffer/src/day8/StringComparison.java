package day8;

public class StringComparison {
    public static void main(String[] args) {
        String str1 = "Programming";
        String str2 = "Programming";

        // Compare using "==" operator
        System.out.println(str1 == str2); // true

        // Compare string objects using equals() methods and "==" operator
        System.out.println(str1.equals(str2)); // true

        String str3 = new String("Programming");
        String str4 = new String("Programming");
        System.out.println(str3 == str4); // False -> "==" compares the objects
        System.out.println(str3.equals(str4)); // true -> compares the values of objects


        String str5 = "Programming";
        String str6 = new String("Programming");
        String str7 = str6;
        System.out.println(str5 == str6);
        System.out.println(str5.equals(str6)); // true -> compare value of different objects using equals() method
        System.out.println(str5 == str7); // false -> compare different objects using "==" operator
        System.out.println(str5.equals(str7)); // true -> compare value of different objects using equals() method
        System.out.println(str7 == str6); // true -> same objects
        System.out.println(str7.equals(str6)); // true -> same objects

    }
}
