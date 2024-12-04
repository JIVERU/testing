package cinema.ticket.testin;

import okhttp3.*;
import java.io.IOException;
import java.util.Base64;

public class PayPalApiHelper {

    private static final String CLIENT_ID = "AWoxi7XjV-S18XpKxCddHLnRzWCDK6xJ5pjF-R850g_xdevNdtSPxShUX5jMnRcy7XRfqQIvUYtlUilA"; // Replace with your actual Client ID
    private static final String CLIENT_SECRET = "EPzXPhLAJoe95kzhnVdADRdpsCiKKJ_jKpknEmTHi5iexc84ohlmfRdH7d3IxTzJGiLuFFFYTANpfEUl"; // Replace with your actual Secret
    private static final String TOKEN_URL = "https://api-m.sandbox.paypal.com/v1/oauth2/token";

    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();

    public static String getAccessToken() throws IOException, NullPointerException {
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials");

        Request request = new Request.Builder()
                .url(TOKEN_URL)
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + encodedCredentials)
                .build();
        System.out.println(request);
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Failed to get access token: " + response);
        }

        String accessToken = null;
        if (response.body() != null) {
            String responseBody = response.body().string();
            accessToken = extractAccessToken(responseBody);
        }else{
            throw new NullPointerException("Response body doesn't exist" + response);
        }
        response.close();
        return accessToken;
    }

    // Helper Method to Parse Access Token from Response JSON
    private static String extractAccessToken(String responseBody) {
        // Assuming Gson is used for JSON parsing (add Gson dependency if not already added)
        com.google.gson.JsonObject jsonResponse = com.google.gson.JsonParser.parseString(responseBody).getAsJsonObject();
        return jsonResponse.get("access_token").getAsString();
    }
    //    private static String getAccessToken() throws IOException, InterruptedException {
//        try {
//            HttpRequest postRequest = HttpRequest.newBuilder()
//                    .uri(URI.create("https://api-m.sandbox.paypal.com/v1/oauth2/token"))
//                    .header("Content-Type", "application/x-www-form-urlencoded")
//                    .header("Authorization","Basic " + encodeCredentials())
//                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
//                    .build();
//
//            HttpResponse<String> postResponse;
//            try (HttpClient client = HttpClient.newHttpClient()) {
//                postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
//            } catch (InterruptedException e) {
//                throw new RuntimeException("Post request was interrupted: " + e);
//            }
//
//            String responseBody = postResponse.body();
//            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
//            return jsonObject.get("access_token").getAsString();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
