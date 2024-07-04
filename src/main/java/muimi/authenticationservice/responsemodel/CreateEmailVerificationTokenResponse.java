package muimi.authenticationservice.responsemodel;

import jakarta.annotation.Nullable;

public record CreateEmailVerificationTokenResponse(
    String status,
    int tokenID,
    @Nullable String verificationToken
) {
}
