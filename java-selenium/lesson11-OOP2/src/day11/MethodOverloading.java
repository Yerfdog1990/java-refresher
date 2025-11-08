package day11;

class Product {
    // Overload 1: Two integer parameters
    public int multiply(int a, int b) {
        return a * b;
    }

    // Overload 2: Three integer parameters (Changed number of parameters)
    public int multiply(int a, int b, int c) {
        return a * b * c;
    }

    // Overload 3: Three double parameters (Changed data types)
    public double multiply(double a, double b, double c) {
        return a * b * c;
    }
}

class Geeks {
    public static void main(String[] args) {
        Product ob = new Product();

        System.out.println("2-int product: " + ob.multiply(2, 3));      // Calls Overload 1
        System.out.println("3-int product: " + ob.multiply(2, 3, 4));   // Calls Overload 2
        System.out.println("3-double product: " + ob.multiply(2.5, 2.0, 1.0)); // Calls Overload 3
    }
}

// Output:
// 2-int product: 6
// 3-int product: 24
// 3-double product: 5.0