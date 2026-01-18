package springsecurity.lesson1topologyofrolesandprivileges.persistance.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = {"privilege", "authorities"}, callSuper = false)
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String privilege;

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> authorities;

    public Privilege(String privilege) {
        this.privilege = privilege;
    }

}