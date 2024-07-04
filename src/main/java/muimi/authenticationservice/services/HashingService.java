package muimi.authenticationservice.services;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HashingService {
    private static final Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(
            16,
            64,
            1,
            7340,
            5
    );

    public String hash(String target) {
        return passwordEncoder.encode(target);
    }

    public boolean verify(String rawTarget, String encodedTarget) {
        return passwordEncoder.matches(rawTarget, encodedTarget);
    }
}
