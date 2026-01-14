package springsecurity.lesson2methodlevelauthorizationwithexpressions.persistance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import springsecurity.lesson2methodlevelauthorizationwithexpressions.validation.PasswordMatches;
import springsecurity.lesson2methodlevelauthorizationwithexpressions.validation.ValidPassword;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

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
    private String password;

    @Transient
    private String passwordConfirmation;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false)
    private LocalDateTime created;

    private boolean enabled;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Authority> authorities = new HashSet<>();

    public Student(Long id) {
        this.id = id;
    }
}
