package muimi.authenticationservice.services;

import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
public class GenerateTotpQrService {
    public String generateQrCodeUri(String secret, String username) throws QrGenerationException {
        QrData data = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer("Muimi")
                .algorithm(HashingAlgorithm.SHA512)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();

        byte[] imageData = generator.generate(data);
        String mimeType = generator.getImageMimeType();

        return getDataUriForImage(imageData, mimeType);
    }
}
