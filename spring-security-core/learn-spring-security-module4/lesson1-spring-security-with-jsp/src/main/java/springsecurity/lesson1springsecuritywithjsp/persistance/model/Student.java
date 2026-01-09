package springsecurity.lesson1springsecuritywithjsp.persistance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import springsecurity.lesson1springsecuritywithjsp.validation.PasswordMatches;
import springsecurity.lesson1springsecuritywithjsp.validation.ValidPassword;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = {"authorities", "verificationToken"})
@EqualsAndHashCode(of = {"email"})
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

    @Column(name = "date_created")
    private Calendar created = Calendar.getInstance();

    private boolean enabled;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Authority> authorities = new HashSet<>();
}
