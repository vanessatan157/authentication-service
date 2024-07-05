package muimi.authenticationservice.controllers;

import jakarta.persistence.EntityNotFoundException;
import muimi.authenticationservice.responsemodel.CreateEmailVerificationTokenResponse;
import muimi.authenticationservice.responsemodel.VerifyEmailVerificationTokenResponse;
import muimi.authenticationservice.services.CreateEmailVerificationTokenService;
import muimi.authenticationservice.services.VerifyEmailVerificationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmailVerificationController {
    private static final Logger log = LoggerFactory.getLogger(EmailVerificationController.class);

    @Value("${application.api-auth-key}")
    private String apiAuthKey;

    @Autowired
    private CreateEmailVerificationTokenService createEmailVerificationTokenService;

    @Autowired
    private VerifyEmailVerificationTokenService verifyEmailVerificationTokenService;

    @PostMapping(path="/email/create-token")
    public @ResponseBody ResponseEntity<CreateEmailVerificationTokenResponse> createToken(
            @RequestHeader(name="Authorization") String authHeader,
            @RequestParam String userID
    ) {
        if (isBadApiKey(authHeader)) {
            CreateEmailVerificationTokenResponse response = new CreateEmailVerificationTokenResponse("BAD_API_KEY",-1, "");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            CreateEmailVerificationTokenResponse response = createEmailVerificationTokenService.createVerificationToken(userID);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            CreateEmailVerificationTokenResponse response = new CreateEmailVerificationTokenResponse("SERVER_ERROR",-1, "");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/email/verify-token")
    public @ResponseBody ResponseEntity<VerifyEmailVerificationTokenResponse> verifyToken(
            @RequestHeader(name="Authorization") String authHeader,
            @RequestParam int tokenID,
            @RequestParam String token
    ) {
        if (isBadApiKey(authHeader)) {
            VerifyEmailVerificationTokenResponse response = new VerifyEmailVerificationTokenResponse("BAD_API_KEY", false, "");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            VerifyEmailVerificationTokenResponse response = verifyEmailVerificationTokenService.verifyEmailVerificationToken(tokenID, token);
            if (!response.valid()) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            VerifyEmailVerificationTokenResponse response = new VerifyEmailVerificationTokenResponse("SERVER_ERROR", false, "");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isBadApiKey(String apiKey) {
        return !apiKey.equals(apiAuthKey);
    }
}
