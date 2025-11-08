package day18.customexception;

public class Dog {
    String name;
    boolean collarOn;
    boolean leashOn;
    boolean muzzleOn;

    public Dog(String name) {
        this.name = name;
    }

    public void putCollar() { collarOn = true; }
    public void putLeash() { leashOn = true; }
    public void putMuzzle() { muzzleOn = true; }

    public void walk() throws DogIsNotReadyException {
        if (collarOn && leashOn && muzzleOn) {
            System.out.println("Let's go for a walk!");
        } else {
            throw new DogIsNotReadyException(name + " is not ready for a walk!");
        }
    }

    public static void main(String[] args) {
        Dog dog = new Dog("Buddy");
        // dog.putCollar();
        dog.putMuzzle();
        dog.putLeash();

        try {
            dog.walk();
        } catch (DogIsNotReadyException e) {
            System.out.println(e.getMessage());
        }
    }
}