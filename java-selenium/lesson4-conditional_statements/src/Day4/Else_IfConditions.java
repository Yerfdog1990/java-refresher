package Day4;

public class Else_IfConditions {
    public static void main(String[] args) {
        int a = 40, b = 50, c = 60;
        if (a > b && a < c) {
            System.out.println(a + " is the largest number.");
        } else if (b > c && b < c) {
            System.out.println(b + " is the smallest number.");
        } else {
            System.out.println(c + " is the largest number.");
        }
    }
}
