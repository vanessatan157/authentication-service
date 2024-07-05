package muimi.authenticationservice.services;

import com.google.gson.Gson;
import muimi.authenticationservice.Utils;
import muimi.authenticationservice.models.EncryptionServiceApiResponse;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

@Service
public class EncryptionService {
    public String encryptContent(String id, String content) throws Exception {
        String baseUrl = System.getenv("ENCRYPTION_SERVICE_HOST") + ":" + System.getenv("ENCRYPTION_SERVICE_PORT");
        String url = "http://%s/crypt/encrypt".formatted(baseUrl);

        String formData = "encryptionType=AES_256&content=%s&id=%s".formatted(content, id);
        String authorizationHeader = System.getenv("ENCRYPTION_SERVICE_API_KEY");

        HttpURLConnection con = Utils.getHttpURLConnection(url, authorizationHeader, formData);
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } finally {
            con.disconnect();
        }

        Gson gson = new Gson();
        EncryptionServiceApiResponse apiResponse = gson.fromJson(response.toString(), EncryptionServiceApiResponse.class);

        String status = apiResponse.getStatus();
        if (status.equals("SUCCESS")) {
            return apiResponse.getEncryptedContent();
        } else {
            throw new Exception("Failed to encrypt secret: " + status);
        }
    }

}
