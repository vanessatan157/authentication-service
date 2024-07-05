package muimi.authenticationservice;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Utils {
    public static HttpURLConnection getHttpURLConnection(String url, String authorizationHeader, String formData) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");

        // Set request headers
        con.setRequestProperty("Authorization", authorizationHeader);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Enable input and output streams
        con.setDoOutput(true);

        // Write data to the connection
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            byte[] postData = formData.getBytes(StandardCharsets.UTF_8);
            wr.write(postData);
        }
        return con;
    }
}
