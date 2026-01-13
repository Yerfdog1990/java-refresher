package springsecurity.lesson3springsecuritycustomexpressions.persistance.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"myUser", "authority"})
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private MyUser myUser;

    private String authority;

    public Authority(MyUser myUser, String authority) {
        this.myUser = myUser;
        this.authority = authority;
    }
}
