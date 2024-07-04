package muimi.authenticationservice.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import muimi.authenticationservice.entities.EmailVerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface EmailVerificationRepository extends CrudRepository<EmailVerificationToken, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE EmailVerificationToken ev SET ev.isValid = false WHERE ev.accountID = :accountID")
    void invalidateTokensByAccountID(String accountID);
}