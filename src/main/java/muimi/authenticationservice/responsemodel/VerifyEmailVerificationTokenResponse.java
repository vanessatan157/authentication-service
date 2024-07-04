package muimi.authenticationservice.responsemodel;

import jakarta.annotation.Nullable;

public record VerifyEmailVerificationTokenResponse(
    String status,
    boolean valid,
    @Nullable String accountID
) {
}
