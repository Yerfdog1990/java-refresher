package day8;

public class StringVsStringBufferVsStringBuilder {
    public static void main(String[] args) {
        // Concatenation using concat()
        String str  = "Programming";
        System.out.println("Before concatenation: " + str); // Programming
        str.concat("languages");
        System.out.println("After concatenation: " + str); // Programming -> Original string unchanged after concatenation

        // Concatenation using append()
        StringBuffer sbuffer = new StringBuffer("Programming");
        sbuffer.append(" languages");
        System.out.println("After concatenation: " + sbuffer);

        // Concatenation using append()
        StringBuffer sbuilder = new StringBuffer("Programming"); // Programming languages -> Mutable
        sbuilder.append(" languages");
        System.out.println("After concatenation: " + sbuilder); // Programming languages -> Mutable

    }
}
