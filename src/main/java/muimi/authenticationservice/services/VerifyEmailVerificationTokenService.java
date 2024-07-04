package muimi.authenticationservice.services;

import muimi.authenticationservice.entities.EmailVerificationToken;
import muimi.authenticationservice.repositories.EmailVerificationRepository;
import muimi.authenticationservice.responsemodel.VerifyEmailVerificationTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VerifyEmailVerificationTokenService {

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private HashingService hashingService;

    public VerifyEmailVerificationTokenResponse verifyEmailVerificationToken(int tokenID, String token) {
        Optional<EmailVerificationToken> matchingToken = emailVerificationRepository.findById(tokenID);
        if (matchingToken.isEmpty()) {
            return new VerifyEmailVerificationTokenResponse("INVALID_TOKEN", false, "");
        }

        EmailVerificationToken verificationToken = matchingToken.get();
        if (!hashingService.verify(token, verificationToken.getHashedToken())) {
            return new VerifyEmailVerificationTokenResponse("INVALID_TOKEN", false, verificationToken.getAccountID());
        }

        if (verificationToken.isConsumed()) {
            return new VerifyEmailVerificationTokenResponse("ALREADY_CONSUMED", false, verificationToken.getAccountID());
        }

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new VerifyEmailVerificationTokenResponse("EXPIRED_TOKEN", false, verificationToken.getAccountID());
        }

        verificationToken.setConsumed(true);
        verificationToken.setConsumedAt(LocalDateTime.now());
        emailVerificationRepository.save(verificationToken);

        return new VerifyEmailVerificationTokenResponse("SUCCESS", true, verificationToken.getAccountID());
    }
}
