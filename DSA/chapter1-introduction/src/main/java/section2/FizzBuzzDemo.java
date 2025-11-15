package section2;



public class FizzBuzzDemo {
    private static final int[] integerArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

    public static void main(String[] args) {
        long startTime1 = System.currentTimeMillis();
        unoptimized();
        long endTime1 = System.currentTimeMillis();
        System.out.println("Unoptimized took " + (endTime1 - startTime1) + " seconds");
        long startTime2 = System.currentTimeMillis();
        optimized();
        long endTime2 = System.currentTimeMillis();
        System.out.println("Optimized took " + (endTime2 - startTime2) + " seconds");
    }
    private static void unoptimized() {
        
        for (int num : integerArray) {
            if (num % 3 == 0 && num % 5 == 0){
                System.out.println(num + " -> FizzBuzz");
            } else if (num % 3 == 0){
                System.out.println(num + " -> Fizz");
            } else if (num % 5 == 0){
                System.out.println(num + " -> Buzz");
            } else {
                System.out.println(num);
            }
        }
    }
    private static void optimized() {
        for (int num : integerArray) {
            if (num % 15 == 0){
                System.out.println(num + " -> FizzBuzz");
            } else if (num % 3 == 0){
                System.out.println(num + " -> Fizz");
            } else if (num % 5 == 0){
                System.out.println(num + " -> Buzz");
            } else {
                System.out.println(num);
            }
        }
    }
}
