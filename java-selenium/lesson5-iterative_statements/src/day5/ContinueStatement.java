package day5;

public class ContinueStatement {
    public static void main(String[] args) {
        // Print odd numbers
        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                continue;
            }
            System.out.println(i);
        }
    }
}
