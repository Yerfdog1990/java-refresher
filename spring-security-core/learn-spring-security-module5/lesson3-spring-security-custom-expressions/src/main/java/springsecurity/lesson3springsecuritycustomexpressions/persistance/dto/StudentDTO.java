package springsecurity.lesson3springsecuritycustomexpressions.persistance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.Student;
import springsecurity.lesson3springsecuritycustomexpressions.validation.ValidPassword;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StudentDTO {
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @ValidPassword
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String passwordConfirmation;

    private LocalDateTime created;

    private boolean enabled;


    public StudentDTO(Long id, String username, String email, LocalDateTime created) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.created = created;
    }

    public static StudentDTO fromEntity(Student student) {
        if (student == null) return null;
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setUsername(student.getUsername());
        dto.setEmail(student.getEmail());
        dto.setCreated(student.getCreated());
        // Note: We don't copy the password for security reasons
        return dto;
    }

    public Student toEntity() {
        Student student = new Student();
        student.setId(this.id);
        student.setUsername(this.username);
        student.setEmail(this.email);
        student.setCreated(this.created);
        student.setPassword(this.password);
        return student;
    }

}