package day11;

public class StaticVsNonStaticMethods {
    private static String name;
    private int age;

    public String getName(String name) {
        return name;
    }
    public static int getAge(int age) {
        System.out.println("My age is " + age);
        return age;
    }
    public static void main(String[] args) {
        StaticVsNonStaticMethods obj = new StaticVsNonStaticMethods();
        String objName = obj.getName("Yerfdog");
        System.out.println("My name is " + objName);
        getAge(12);

        // Static methods can access static variables directly without the need for an object.
        name = "Yerfdog";
        System.out.println("My name is " + name);

        // They cannot access non-static variables (instance) or methods directly.
        // obj.age = 12; // Compile error
        // obj.getName("Yerfdog"); // Compile error

    }
}
