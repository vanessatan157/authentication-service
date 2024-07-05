package muimi.authenticationservice.responsemodel;

public record VerifyTotpCodeResponse(
        String status,
        boolean valid
) {
}
