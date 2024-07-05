package muimi.authenticationservice.models;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class EncryptionServiceApiResponse {
    private String status;

    @Nullable
    private String encryptedContent;
}
