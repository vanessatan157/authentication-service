package muimi.authenticationservice.services;

import muimi.authenticationservice.entities.EmailVerificationToken;
import muimi.authenticationservice.repositories.EmailVerificationRepository;
import muimi.authenticationservice.responsemodel.CreateEmailVerificationTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreateEmailVerificationTokenService {
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private GenerateTokenService generateTokenService;

    @Autowired
    private HashingService hashingService;

    public CreateEmailVerificationTokenResponse createVerificationToken(String accountID) {
        String newToken = generateTokenService.generateToken();
        String hashedToken = hashingService.hash(newToken);

        EmailVerificationToken newEmailVerificationToken = createVerificationTokenORM(accountID, hashedToken);
        EmailVerificationToken insertedToken = emailVerificationRepository.save(newEmailVerificationToken);

        return new CreateEmailVerificationTokenResponse("SUCCESS", insertedToken.getId(), newToken);
    }

    private EmailVerificationToken createVerificationTokenORM(
            String accountID,
            String hashedToken
    ) {
        EmailVerificationToken newEmailVerificationToken = new EmailVerificationToken();
        newEmailVerificationToken.setAccountID(accountID);
        newEmailVerificationToken.setHashedToken(hashedToken);

        // Expires 1 hour from now...
        newEmailVerificationToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        return newEmailVerificationToken;
    }

}
