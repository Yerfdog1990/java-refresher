package junit.model.repository;

import lombok.Data;

@Data
public class Student {
    private String firstName;
    private String lastName;
    private YearGroup yearGroup;
    private Subject subject;
    private String email;

    public Student(String firstName, String lastName, YearGroup yearGroup, Subject subject, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.yearGroup = yearGroup;
        this.subject = subject;
        this.email = email;
    }
}
