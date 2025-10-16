package Day4;

public class PositiveOrNegative {
    public static void main(String[] args) {
        int number = 6;
        if (number < 0) {
            System.out.println(number+ " is a negative value.");
        } else if (number > 0) {
            System.out.println(number+ " is a positive value.");
        } else {
            System.out.println(number+ " is a zero value.");
        }
    }
}
