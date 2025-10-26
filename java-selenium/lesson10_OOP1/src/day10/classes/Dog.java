package day10.classes;

public class Dog {
    String name;
    String breed;
    int age;
    String color;

    // Constructor
    public Dog(String name, String breed, int age, String color) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.color = color;
    }

    // Methods
    public String getName() { return name; }
    public String getBreed() { return breed; }
    public int getAge() { return age; }
    public String getColor() { return color; }

    @Override
    public String toString() {
        return ("Name: " + this.getName()
                + "\nBreed, Age, Color: " + this.getBreed() + ", "
                + this.getAge() + ", " + this.getColor());
    }

    public static void main(String[] args) {
        Dog tuffy = new Dog("Tuffy", "Papillon", 5, "White");
        System.out.println(tuffy.toString());
    }
}