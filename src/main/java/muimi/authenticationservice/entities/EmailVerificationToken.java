package muimi.authenticationservice.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a token used for verifying an account.
 */
@Setter
@Getter
@Entity
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @Column(length = 128, nullable = false)
    private String accountID;

    @Column(nullable = false, length = 255)
    private String hashedToken;

    @Column(nullable = false)
    private boolean consumed = false;

    /**
     * Left null indicates never consumed...
     */
    @Column(nullable = true)
    private LocalDateTime consumedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

