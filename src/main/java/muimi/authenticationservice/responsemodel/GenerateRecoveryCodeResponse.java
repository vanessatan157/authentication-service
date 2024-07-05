package muimi.authenticationservice.responsemodel;

import jakarta.annotation.Nullable;

public record GenerateRecoveryCodeResponse(
        String status,
        @Nullable String[] codes
) {
}
