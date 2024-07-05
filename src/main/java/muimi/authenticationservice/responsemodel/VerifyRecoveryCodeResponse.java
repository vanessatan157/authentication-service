package muimi.authenticationservice.responsemodel;

public record VerifyRecoveryCodeResponse(
    String status,
    boolean valid
) {
}
