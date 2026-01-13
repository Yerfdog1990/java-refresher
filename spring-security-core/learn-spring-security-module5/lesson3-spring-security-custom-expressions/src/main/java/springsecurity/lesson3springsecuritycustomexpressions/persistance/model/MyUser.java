package springsecurity.lesson3springsecuritycustomexpressions.persistance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import springsecurity.lesson3springsecuritycustomexpressions.validation.PasswordMatches;
import springsecurity.lesson3springsecuritycustomexpressions.validation.ValidPassword;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"email", "password"})
@NoArgsConstructor
@PasswordMatches
public class MyUser {
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
    private String password;

    @Transient
    private String passwordConfirmation;

    @Column(name = "date_created")
    private LocalDateTime created = LocalDateTime.now();

    private boolean enabled;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Authority> authorities = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_privileges",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private Set<Privilege> privileges = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;
}
