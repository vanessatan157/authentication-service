package muimi.authenticationservice.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Recovery Code for TOTP
 */
@Getter
@Setter
@Entity
@Table(indexes = @Index(columnList = "accountID"))
public class RecoveryCode {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Column(nullable = false, length = 127)
    private String accountID;

    @Column(nullable = false, length = 512)
    private String encryptedCode;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
