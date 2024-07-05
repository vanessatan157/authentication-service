package muimi.authenticationservice.services;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.persistence.EntityNotFoundException;
import muimi.authenticationservice.entities.TotpKey;
import muimi.authenticationservice.repositories.TotpKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerifyTotpCodeService {
    @Autowired
    private DecryptionService decryptionService;

    @Autowired
    private TotpKeyRepository totpKeyRepository;

    public boolean verifyCode(String accountID, String code) throws Exception {
        TotpKey secret = totpKeyRepository.findByAccountID(accountID);
        if (secret == null) {
            throw new EntityNotFoundException("Secret not found");
        }

        String secretKey = decryptionService.decryptContent(accountID, secret.getEncryptedSecretKey());

        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        verifier.setAllowedTimePeriodDiscrepancy(2);

        return verifier.isValidCode(secretKey, code);
    }
}
