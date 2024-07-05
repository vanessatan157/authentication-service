package muimi.authenticationservice.services;
import java.security.SecureRandom;
import org.springframework.stereotype.Service;

/**
 * Service to generate an Alphanumerical string token
 */
@Service
public class GenerateTokenService {
    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom random = new SecureRandom();

    /**
     *
     * @return A randomly generate alphanumerical string of length 32.
     */
    public String generateToken(){
        return generateToken(32);
    }

    public String generateToken(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUMERIC_STRING.length());
            stringBuilder.append(ALPHANUMERIC_STRING.charAt(index));
        }

        return stringBuilder.toString();
    }
}
