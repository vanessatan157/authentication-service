package muimi.authenticationservice.repositories;

import muimi.authenticationservice.entities.EmailVerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface EmailVerificationRepository extends CrudRepository<EmailVerificationToken, Integer> {

}