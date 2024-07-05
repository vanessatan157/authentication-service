package muimi.authenticationservice.services;

import muimi.authenticationservice.entities.RecoveryCode;
import muimi.authenticationservice.repositories.RecoveryCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class VerifyRecoveryCodeService {

    @Autowired
    private DecryptionService decryptionService;

    @Autowired
    private RecoveryCodeRepository recoveryCodeRepository;

    public boolean verifyRecoveryCode(String accountID, String code) throws Exception {

        List<RecoveryCode> recoveryCodes = recoveryCodeRepository.findAllByAccountID(accountID);

        // NOTE: Decryption service makes an HTTP API call, this should be done in parallel to improve performance
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(recoveryCodes.size(), 10));
        List<CompletableFuture<Boolean>> futures = recoveryCodes.stream()
                .map(recoveryCode -> CompletableFuture.supplyAsync(() -> {
                    try {
                        String decryptedCode = decryptionService.decryptContent(accountID, recoveryCode.getEncryptedCode());
                        return decryptedCode.equals(code);
                    } catch (Exception e) {
                        // Handle decryption exception if needed
                        e.printStackTrace();
                        return false;
                    }
                }, executorService))
                .toList();

        // Wait for all futures to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        // Get the result of each CompletableFuture
        boolean isValidCode = futures.stream()
                .anyMatch(CompletableFuture::join); // Check if any result is true

        // Shutdown the executor service
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Delete recovery codes if a valid code was found
        if (isValidCode) {
            recoveryCodeRepository.deleteByAccountID(accountID);
        }

        return isValidCode;
    }

}
