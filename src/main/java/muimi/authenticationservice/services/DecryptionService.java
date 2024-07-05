package muimi.authenticationservice.services;

import com.google.gson.Gson;
import muimi.authenticationservice.Utils;
import muimi.authenticationservice.models.DecryptionServiceApiResponse;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

@Service
public class DecryptionService {

    public String decryptContent(String id, String content) throws Exception {
        String baseUrl = System.getenv("ENCRYPTION_SERVICE_HOST") + ":" + System.getenv("ENCRYPTION_SERVICE_PORT");
        String url = "http://%s/crypt/decrypt".formatted(baseUrl);

        String formData = "content=%s&id=%s".formatted(content, id);
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
        DecryptionServiceApiResponse apiResponse = gson.fromJson(response.toString(), DecryptionServiceApiResponse.class);

        String status = apiResponse.getStatus();
        if (status.equals("SUCCESS")) {
            return apiResponse.getDecryptedContent();
        } else {
            throw new Exception("Failed to encrypt secret: " + status);
        }
    }
}
