package muimi.authenticationservice.services;

import com.google.gson.Gson;
import muimi.authenticationservice.models.DecryptionServiceApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class DecryptionService {
    private final RestTemplate restTemplate ;

    @Autowired
    public DecryptionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String decryptContent(String id, String content) throws Exception {
        String baseUrl = System.getenv("ENCRYPTION_SERVICE_HOST") + ":" + System.getenv("ENCRYPTION_SERVICE_PORT");
        String url = "http://%s/crypt/decrypt".formatted(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", System.getenv("ENCRYPTION_SERVICE_API_KEY"));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
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
        DecryptionServiceApiResponse apiResponse = gson.fromJson(responseEntity.getBody(), DecryptionServiceApiResponse.class);

        String status = apiResponse.getStatus();
        if (status.equals("SUCCESS")) {
            return apiResponse.getDecryptedContent();
        } else {
            throw new Exception("Failed to encrypt secret: " + status);
        }
    }
}
