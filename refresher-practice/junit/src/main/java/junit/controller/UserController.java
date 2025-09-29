package junit.controller;

import junit.model.repository.*;
import junit.model.service.UserService;

import java.util.List;
import java.util.Scanner;

public class UserController {
    private final UserService userService;
    private final Scanner scanner = new Scanner(System.in);

    public UserController(UserService userService) {
        this.userService = userService;
    }
    public void run(){
        System.out.println("Welcome to the class registration system. Type \"help\" for help commands.");
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if(line.isEmpty()) continue;
            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();
            try{
                switch (cmd) {
                    case "help": printHelp();
                        break;
                    case "register":
                        // Usage: register a student
                        Student student1 = new Student("Joe", "Doe", YearGroup.YEAR_13, Subject.PHYSICS, "jdoe@example.com");
                        userService.registerStudent(1, student1, RegistrationStatus.PRESENT);

                        Student student2 = new Student("Alice", "Smith", YearGroup.YEAR_12, Subject.MATHEMATICS, "asmith@example.com");
                        userService.registerStudent(2, student2, RegistrationStatus.PRESENT);

                        Student student3 = new Student("Bob", "Johnson", YearGroup.YEAR_10, Subject.ENGLISH, "bjohnson@example.com");
                        userService.registerStudent(3, student3, RegistrationStatus.ABSENT_WITHOUT_PERMISSION);

                        Student student4 = new Student("Charlie", "Williams", YearGroup.YEAR_11, Subject.SCIENCE, "cwilliams@example.com");
                        userService.registerStudent(4, student4, RegistrationStatus.OUT_OF_SCHOOL);

                        Student student5 = new Student("David", "Brown", YearGroup.YEAR_9, Subject.LITERATURE, "dbrown@example.com");
                        userService.registerStudent(5, student5, RegistrationStatus.PRESENT);

                        Student student6 = new Student("Emily", "Taylor", YearGroup.YEAR_8, Subject.HISTORY, "etaylor@example.com");
                        userService.registerStudent(6, student6, RegistrationStatus.ABSENT_WITH_PERMISSION);

                        Student student7 = new Student("Frank", "Miller", YearGroup.YEAR_13, Subject.PHYSICS, "fmiller@example.com");
                        userService.registerStudent(7, student7, RegistrationStatus.PRESENT);

                        Student student8 = new Student("Grace", "Davis", YearGroup.YEAR_12, Subject.MATHEMATICS, "gdavis@example.com");
                        userService.registerStudent(8, student8, RegistrationStatus.ABSENT_WITHOUT_PERMISSION);

                        Student student9 = new Student("Henry", "Wilson", YearGroup.YEAR_10, Subject.ENGLISH, "hwilson@example.com");
                        userService.registerStudent(9, student9, RegistrationStatus.OUT_OF_SCHOOL);

                        Student student10 = new Student("Ivy", "Anderson", YearGroup.YEAR_11, Subject.SCIENCE, "ianderson@example.com");
                        userService.registerStudent(10, student10, RegistrationStatus.PRESENT);
                        System.out.println("Student registered successfully.");
                        break;
                    case "list":
                        List<ClassRegister> registerList = userService.findAll();
                        registerList.forEach(System.out::println);
                        break;
                    case "get":
                        System.out.println(userService.findById(2));
                        break;
                    case "delete":
                        userService.deleteById(1);
                        System.out.println("Student deleted successfully.");
                        break;
                    case "exit":
                        System.out.println("Bye bye!");
                        return;
                    default: System.out.println("Type \"help\" for guidance.");
                }
            }catch (Exception e){
                System.out.println("Invalid command.");
            }
        }
    }
    private void printHelp() {
        System.out.println("Welcome to the class registration system.");
        System.out.println("Commands:");
        System.out.println("  register <id> <firstName> <lastName> <year group> <subject name> <email> <registration status> - create a new student.");
        System.out.println("  list                                                                                           - list all students.");
        System.out.println("  get <id>                                                                                       - find a student by ID.");
        System.out.println("  delete <id>                                                                                    - delete a student by ID.");
        System.out.println("  exit                                                                                           - exit!de");

    }
}
