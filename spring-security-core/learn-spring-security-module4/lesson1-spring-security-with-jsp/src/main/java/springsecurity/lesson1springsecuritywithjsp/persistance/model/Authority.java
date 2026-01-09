package springsecurity.lesson1springsecuritywithjsp.persistance.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = "student")
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "authority"})
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private String authority;

    public Authority(Student student, String authority) {
        this.student = student;
        this.authority = authority;
    }
}
