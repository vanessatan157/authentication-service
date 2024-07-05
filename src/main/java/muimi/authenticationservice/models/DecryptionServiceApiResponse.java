package muimi.authenticationservice.models;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class DecryptionServiceApiResponse {
    private String status;

    @Nullable
    private String decryptedContent;
}
