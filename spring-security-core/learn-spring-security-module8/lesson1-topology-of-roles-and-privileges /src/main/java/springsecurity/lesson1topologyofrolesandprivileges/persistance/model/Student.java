package springsecurity.lesson1topologyofrolesandprivileges.persistance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import springsecurity.lesson1topologyofrolesandprivileges.validation.PasswordMatches;
import springsecurity.lesson1topologyofrolesandprivileges.validation.ValidPassword;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Data
@EqualsAndHashCode(of = {"email", "password"})
@NoArgsConstructor
@PasswordMatches
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Username is required")
    private String username;

    @Email
    @NotEmpty(message = "Email is required.")
    private String email;

    @NotEmpty(message = "Password is required.")
    @ValidPassword
    @Column(length = 100)
    private String password;

    @Transient
    private String passwordConfirmation;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime created;

    private boolean enabled;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private VerificationToken verificationToken;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_roles", joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public Student(Long id) {
        this.id = id;
    }

    public Student(String name, String email, String password) {
        this.username = name;
        this.email = email;
        this.password = password;
    }

    // For testing purposes
    public Student(String email) {
        this.email = email;
    }
}
