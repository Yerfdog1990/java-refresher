package Day4;

public class SwitchStatement {
    public static void main(String[] args) {
        int day = 8;

        switch (day) {
            case 1:
                System.out.println("It is on a Monday.");
                break;
            case 2:
                System.out.println("It is on a Tuesday.");
                break;
            case 3:
                System.out.println("It is on a Wednesday.");
                break;
            case 4:
                System.out.println("It is on a Thursday.");
                break;
            case 5:
                System.out.println("It is on a Friday.");
                break;
            case 6:
                System.out.println("It is on a Saturday.");
                break;
            case 7:
                System.out.println("It is on a Sunday.");
                break;
            default:
                throw new RuntimeException("Invalid day");
        }
    }
}
