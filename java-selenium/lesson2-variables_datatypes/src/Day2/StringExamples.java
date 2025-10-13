package Day2;

public class StringExamples {
    static void main(String[] args) {
        String name = "Amigo";
        String city = "Rome";
        String message = "Hello, " + name + " from " + city + "!";

        System.out.println(message);
        System.out.println("Message length: " + message.length());
        System.out.println("Uppercase: " + message.toUpperCase());
        System.out.println("Lowercase: " + message.toLowerCase());
    }
}
