package Day4;

public class NestedCondition {
    public static void main(String[] args) {
        int day = 6;
        if (day > 0 && day <= 5) {
            System.out.println("It is a week day.");
            if (day == 1){
                System.out.println("It is Monday");
            } else if (day == 2) {
                System.out.println("It is Tuesday");
            } else if (day == 3) {
                System.out.println("It is Wednesday");
            } else if (day == 4) {
                System.out.println("It is Thursday");
            } else if (day == 5) {
                System.out.println("It is Friday");
            }
        } else if (day >= 6 && day <= 7) {
            System.out.println("It is a weekend.");
            if (day == 6) {
            System.out.println("It is Saturday");
            } else if (day == 7) {
                System.out.println("It is Sunday");
            }
        } else {
            throw new AssertionError("Invalid day");
        }
    }
}
