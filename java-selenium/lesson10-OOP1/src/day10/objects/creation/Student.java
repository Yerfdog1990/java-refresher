package day10.objects.creation;

import java.io.*;
import java.lang.reflect.Constructor;

class Student implements Cloneable, Serializable {
    int id;
    String name;

    // Constructor to initialize fields
    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static void main(String[] args) {
        // Method 1: Creating an object of a class using "new" keyword
        Student s1 = new Student(10, "Alice");
        System.out.println("Creating object using \"new\" keyword: " + s1);

        // Method 2: Creating an object of a class using reflection
        try {
            // Step 1. Get the Class object
            Class<?> studentClass = Student.class;

            // Step 2. Get the constructor with parameter types
            Class<?>[] paramTypes = {int.class, String.class};
            Constructor<?> constructor = studentClass.getConstructor(paramTypes);

            // Step 3. Create new instance with arguments
            Object[] constructorArgs = {20, "John Doe"};
            Student student = (Student) constructor.newInstance(constructorArgs);

            // Step 4. Now you can work with the created object
            System.out.println("Creating object using reflection: " + student);
        } catch (Exception e) {
            System.err.println(e.getMessage());;
        }
        // Method 3: Creating an object using clone()
        try {
            Student s2 = (Student) s1.clone();
            System.out.println("Creating object using clone(): " + s2);

            // Verify it's a different object
            System.out.println("Original and clone are " + (s1 == s2 ? "same" : "different") + " objects");
            System.out.println("Original and clone have " + (s1.equals(s2) ? "same" : "different") + " content");

        } catch (CloneNotSupportedException e) {
            System.err.println("Cloning not supported: " + e.getMessage());
        }
        // Method 4: Creating an object using Deserialization
        // Step 1. Serialization
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("student.ser"))) {
            System.out.print("Creating object using Deserialization: ");
            out.writeObject(new Student(20, "Alice"));
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        // Step 2. Deserialization
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("student.ser"))) {
            Student s = (Student) in.readObject();
            System.out.println(s);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

