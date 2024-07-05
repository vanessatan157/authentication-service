package muimi.authenticationservice.services;

import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import muimi.authenticationservice.entities.RecoveryCode;
import muimi.authenticationservice.repositories.RecoveryCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class CreateRecoveryCodesService {
    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private RecoveryCodeRepository recoveryCodeRepository;

    /**
     * Create 16 Recovery Codes for an account.
     * Previous Recovery Codes will be revoked.
     */
    public String[] createRecoveryCodes(String accountID) throws Exception {
        RecoveryCodeGenerator recoveryCodes = new RecoveryCodeGenerator();
        String[] codes = recoveryCodes.generateCodes(16);

        // NOTE: As encryption service makes an HTTP API call,
        // this should be done in parallel to improve performance.
        List<RecoveryCode> recoveryCodesList = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(codes.length, 10));

        for (String code : codes) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    String encryptedCode = encryptionService.encryptContent(accountID, code);
                    RecoveryCode recoveryCode = new RecoveryCode();
                    recoveryCode.setAccountID(accountID);
                    recoveryCode.setEncryptedCode(encryptedCode);
                    recoveryCodesList.add(recoveryCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService);
            futures.add(future);
        }

        // Block and Wait for all futures to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.get();

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        recoveryCodeRepository.deleteByAccountID(accountID);
        recoveryCodeRepository.saveAll(recoveryCodesList);
        return codes;
    }
}
