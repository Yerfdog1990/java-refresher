package springsecurity.lesson2datastructureofacl.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"name", "owner"})
public class Possession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Student owner;

    // Optional ACL inheritance
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Possession parent;
}

