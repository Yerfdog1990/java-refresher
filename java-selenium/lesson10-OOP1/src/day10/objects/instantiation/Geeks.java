package day10.objects.instantiation;

public class Geeks {
    private String name;
    private float price;

    // Parameterized constructor
    public Geeks(String name, float price) {
        this.name = name;
        this.price = price;
    }

    // No-arg constructor
    public Geeks() {
        // Default initialization
        this.name = "Default";
        this.price = 0.0f;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Geeks{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

    public static void main(String[] args) {
        // Method 1: Initialize using parameterized constructor
        Geeks tuffy = new Geeks("Tuffy", 0.5f);
        System.out.println("Object initialized using parameterized constructor: " + tuffy);

        // Method 2: Initialize using no-arg constructor and setters
        Geeks defaultGeeks = new Geeks();
        System.out.println("Object initialized using no-arg constructor: " + defaultGeeks);

        // Update using setters
        defaultGeeks.setName("Updated");
        defaultGeeks.setPrice(10.0f);
        System.out.println("After updating using setters: " + defaultGeeks);

        // Method 3: Using anonymous object with parameterized constructor
        String getName = new Geeks("Visual Studio", 0.0f).getName();
        double getPrice = new Geeks("Visual Studio", 0.0f).getPrice();
        System.out.println("Anonymous object: " + "Geeks{name'" + getName + "', price=" + getPrice + "}");
    }
}