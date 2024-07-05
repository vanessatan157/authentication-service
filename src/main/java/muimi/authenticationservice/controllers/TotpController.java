package muimi.authenticationservice.controllers;

import muimi.authenticationservice.responsemodel.CreateTotpResponse;
import muimi.authenticationservice.responsemodel.VerifyEmailVerificationTokenResponse;
import muimi.authenticationservice.services.GenerateTotpQrService;
import muimi.authenticationservice.services.GenerateTotpSecretService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TotpController {
    private static final Logger log = LoggerFactory.getLogger(TotpController.class);

    @Value("${application.api-auth-key}")
    private String apiAuthKey;

    @Autowired
    private GenerateTotpQrService generateTotpQrService;

    @Autowired
    private GenerateTotpSecretService generateTotpSecretService;

    @PostMapping(path="/totp/generate-token")
    public @ResponseBody ResponseEntity<CreateTotpResponse> verifyToken(
            @RequestHeader(name="Authorization") String authHeader,
            @RequestParam String accountID,
            @RequestParam String username
    ) {
        if (isBadApiKey(authHeader)) {
            CreateTotpResponse response = new CreateTotpResponse("BAD_API_KEY", null, null);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            String secret = generateTotpSecretService.generateTotp(accountID);
            String qrCodeUri = generateTotpQrService.generateQrCodeUri(secret, username);
            CreateTotpResponse response = new CreateTotpResponse("SUCCESS", secret, qrCodeUri);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            CreateTotpResponse response = new CreateTotpResponse("SERVER_ERROR", null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isBadApiKey(String apiKey) {
        return !apiKey.equals(apiAuthKey);
    }
}
