package muimi.authenticationservice;

import static org.assertj.core.api.Assertions.assertThat;

import muimi.authenticationservice.controllers.EmailVerificationController;
import muimi.authenticationservice.responsemodel.CreateEmailVerificationTokenResponse;
import muimi.authenticationservice.responsemodel.VerifyEmailVerificationTokenResponse;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.http.*;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ContextConfiguration(classes=AuthenticationServiceApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class AuthenticationServiceApplicationTests {
    @Autowired
    private EmailVerificationController emailVerificationController;

    private static final Random random = new Random();

    private static final String testApiKey ="testingKey";

    private static String generateRandomAlphanumericalString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char ch = (char) ThreadLocalRandom.current().nextInt(' ', '~' + 1);
            sb.append(ch);
        }
        return sb.toString();
    }

    static Stream<Object[]> generateEmailVerificationTokenInputs() {
        return Stream.generate(() -> new Object[] {
                generateRandomAlphanumericalString(random.nextInt(127) + 1)
        }).limit(50);
    }

    @ParameterizedTest
    @MethodSource("generateEmailVerificationTokenInputs")
    void testEmailVerificationController(String userID) {
        System.out.println("Generated UserID :: " + userID);

        // Create token and assert success.
        ResponseEntity<CreateEmailVerificationTokenResponse> createdTokenResponse = emailVerificationController.createToken(testApiKey, userID);
        assertEquals(HttpStatus.OK, createdTokenResponse.getStatusCode());
        assertEquals("SUCCESS", Objects.requireNonNull(createdTokenResponse.getBody()).status());
        assertThat(createdTokenResponse.getBody().tokenID()).isPositive();
        assertThat(createdTokenResponse.getBody().verificationToken()).isNotBlank();
        System.out.println("Generated Token (" + createdTokenResponse.getBody().verificationToken() + ") with ID of " + createdTokenResponse.getBody().tokenID());

        // Verify token and assert success.
        ResponseEntity<VerifyEmailVerificationTokenResponse> verificationResponse = emailVerificationController.verifyToken(testApiKey, createdTokenResponse.getBody().tokenID(), createdTokenResponse.getBody().verificationToken());
        assertEquals(HttpStatus.OK, verificationResponse.getStatusCode());
        assertEquals("SUCCESS", Objects.requireNonNull(verificationResponse.getBody()).status());
        assertTrue((verificationResponse.getBody().valid()));
        assertEquals(userID, verificationResponse.getBody().accountID());

        // Verify token consumption, assert failure due to already consumed.
        ResponseEntity<VerifyEmailVerificationTokenResponse> consumedVerificationResponse = emailVerificationController.verifyToken(testApiKey, createdTokenResponse.getBody().tokenID(), createdTokenResponse.getBody().verificationToken());
        assertEquals(HttpStatus.BAD_REQUEST, consumedVerificationResponse.getStatusCode());
        assertEquals("ALREADY_CONSUMED", Objects.requireNonNull(consumedVerificationResponse.getBody()).status());
        assertFalse((consumedVerificationResponse.getBody().valid()));
        assertEquals(userID, consumedVerificationResponse.getBody().accountID());
    }
}
