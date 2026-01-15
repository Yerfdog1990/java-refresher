package springsecurity.lesson1customauthenticationprovider.persistance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import springsecurity.lesson1customauthenticationprovider.validation.PasswordMatches;
import springsecurity.lesson1customauthenticationprovider.validation.ValidPassword;

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
    @Column(length = 100)  // Increased length to store bcrypt hashes
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
