package springsecurity.lesson2basicsecurityconfiguration.persistence.entity;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.Calendar;

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

    private Calendar dateCreated = Calendar.getInstance();

    @Column(unique = true, nullable = false)
    private String password;
}
