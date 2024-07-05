package muimi.authenticationservice.repositories;

import jakarta.transaction.Transactional;
import muimi.authenticationservice.entities.TotpKey;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TotpKeyRepository extends CrudRepository<TotpKey, Integer> {
    TotpKey findByAccountID(String accountID);

    @Transactional
    @Modifying
    @Query("DELETE FROM TotpKey tk WHERE tk.accountID = :accountID")
    void deleteByAccountID(String accountID);
}
