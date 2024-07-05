package muimi.authenticationservice.services;

import dev.samstevens.totp.secret.SecretGenerator;
import muimi.authenticationservice.entities.TotpKey;
import muimi.authenticationservice.repositories.TotpKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.samstevens.totp.secret.DefaultSecretGenerator;

@Service
public class GenerateTotpSecretService {

    @Autowired
    private TotpKeyRepository totpKeyRepository;

    @Autowired
    private EncryptionService encryptionService;

    public String generateTotp(String accountID) throws Exception {
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String secret = secretGenerator.generate();

        String encryptedSecret = encryptionService.encryptContent(accountID, secret);
        totpKeyRepository.deleteByAccountID(accountID);
        TotpKey newTotpKey = new TotpKey();
        newTotpKey.setAccountID(accountID);
        newTotpKey.setEncryptedSecretKey(encryptedSecret);
        totpKeyRepository.save(newTotpKey);
        return secret;
    }
}
