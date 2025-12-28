package springsecurity.lesson6ensurepasswordstrength.persistance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"student", "questionDefinition", "answer"})
public class SecurityQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(targetEntity = Student.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, unique = true)
    private Student student;
    @OneToOne(targetEntity = SecurityQuestionDefinition.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private SecurityQuestionDefinition questionDefinition;
    private String answer;

    // Constructor
    public SecurityQuestion(Student student, SecurityQuestionDefinition questionDefinition, String answer) {
        this.student = student;
        this.questionDefinition = questionDefinition;
        this.answer = answer;
    }
}