package springsecurity.lesson7customlogoutform.persistence.entity;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.Calendar;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = {"username", "email"})
@Entity
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    private String username;

    @NaturalId
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, name = "date_created")
    private Calendar dateCreated = Calendar.getInstance();

    @Column(unique = true, nullable = false)
    private String password;

    @Column(nullable = false)
    boolean enabled = true;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Authority> authorities;
}
