package springsecurity.lesson3springsecuritycustomexpressions.persistance.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"token", "myUser"})
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = MyUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private MyUser myUser;

    private Date expiryDate;

    public PasswordResetToken(final String token, final MyUser myUser) {
        super();

        this.token = token;
        this.myUser = myUser;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Token [String=").append(token).append("]").append("[Expires").append(expiryDate).append("]");
        return builder.toString();
    }
}
