package muimi.authenticationservice.services;

import com.google.gson.Gson;
import muimi.authenticationservice.models.EncryptionServiceApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class EncryptionService {
    private final RestTemplate restTemplate ;

    @Autowired
    public EncryptionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String encryptContent(String id, String content) throws Exception {
        String baseUrl = System.getenv("ENCRYPTION_SERVICE_HOST") + ":" + System.getenv("ENCRYPTION_SERVICE_PORT");
        String url = "http://%s/crypt/encrypt".formatted(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", System.getenv("ENCRYPTION_SERVICE_API_KEY"));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("encryptionType", "AES_256");
        body.add("content", content);
        body.add("id", id);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        Gson gson = new Gson();
        EncryptionServiceApiResponse apiResponse = gson.fromJson(responseEntity.getBody(), EncryptionServiceApiResponse.class);

        String status = apiResponse.getStatus();
        if (status.equals("SUCCESS")) {
            return apiResponse.getEncryptedContent();
        } else {
            throw new Exception("Failed to encrypt secret: " + status);
        }
    }
}
