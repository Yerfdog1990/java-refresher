package day5;

public class WhileLoopWithoutCondition {
    public static void main(String[] args) {
        int number = 1;
        while (true){
            System.out.printf("number = %d\n", number);
            number++;
            if (number == 10) {
                break;
            }
        }
    }
}
