package muimi.authenticationservice.responsemodel;

import jakarta.annotation.Nullable;

public record CreateTotpResponse(
    String status,
    @Nullable String secret,
    @Nullable String qrCodeUri
) {
}
