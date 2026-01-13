package springsecurity.lesson3springsecuritycustomexpressions.persistance.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Organization(String name) {
        this.name = name;
    }

}