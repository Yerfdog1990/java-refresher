package springsecurity.lesson3springsecuritycustomexpressions.persistance.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"myUser", "questionDefinition", "answer"})
public class SecurityQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(targetEntity = MyUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, unique = true)
    private MyUser myUser;
    @OneToOne(targetEntity = SecurityQuestionDefinition.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private SecurityQuestionDefinition questionDefinition;
    private String answer;

    // Constructor
    public SecurityQuestion(MyUser myUser, SecurityQuestionDefinition questionDefinition, String answer) {
        this.myUser = myUser;
        this.questionDefinition = questionDefinition;
        this.answer = answer;
    }
}