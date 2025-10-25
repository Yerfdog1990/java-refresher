package day8;

public class ReverseString {
    public static void main(String[] args) {
        // Method 1: Retrieving character from the string
        String str1 = "Programming";
        String rev1 = "";
        for (int i = str1.length() - 1; i >= 0; i--) {
            rev1 =  rev1 + str1.charAt(i);
        }
        System.out.println("Reversed string method 1: " + rev1);

        // Method 2: Converting string to char array type
        String str2 = "Programming";
        String rev2 = "";
        char[] chars = str2.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            rev2 = rev2 + chars[i];
        }
        System.out.println("Reversed string method 2: " + rev2);

        // Method 3: Store character retrieve in StringBuilder
        String str3 = "Programming";
        StringBuffer sbuffer = new StringBuffer(str3);
        sbuffer.reverse();
        System.out.println("Reversed string method 3: " + sbuffer);

        // Method 4: Store character retrieved in StringBuffer
        String str4 = "Programming";
        StringBuffer sbuilder = new StringBuffer(str4);
        sbuilder.reverse();
        System.out.println("Reversed string method 4: " + sbuilder);
    }
}
