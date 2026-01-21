package springsecurity.lesson2datastructureofacl.persistence.entity;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"username", "email"})
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    private String username;

    @NaturalId
    @Column(unique = true, nullable = false)
    private String email;

    private LocalDateTime dateCreated = LocalDateTime.now();

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Authority> authorities = new HashSet<>();

    public Student(Long id) {
        this.id = id;
    }
}

