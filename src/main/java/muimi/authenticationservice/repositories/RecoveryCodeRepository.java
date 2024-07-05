package muimi.authenticationservice.repositories;

import jakarta.transaction.Transactional;
import muimi.authenticationservice.entities.RecoveryCode;
import muimi.authenticationservice.entities.TotpKey;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecoveryCodeRepository extends CrudRepository<RecoveryCode, Integer> {
    void deleteByAccountID(String accountID);
    List<RecoveryCode> findAllByAccountID(String accountID);
    <S extends RecoveryCode> List<S> saveAll(Iterable<S> entities);
}
