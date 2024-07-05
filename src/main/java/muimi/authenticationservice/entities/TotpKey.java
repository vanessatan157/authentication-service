package muimi.authenticationservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class TotpKey {

    /**
     * Should be the account's ID
     */
    @Id
    @Column(nullable = false, length = 128)
    private String accountID;

    @Column(nullable = false, length = 255)
    private String encryptedSecretKey;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
