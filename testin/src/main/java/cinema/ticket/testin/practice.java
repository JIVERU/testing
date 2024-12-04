package cinema.ticket.testin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class practice {
    private static final String CLIENT_ID = "AWoxi7XjV-S18XpKxCddHLnRzWCDK6xJ5pjF-R850g_xdevNdtSPxShUX5jMnRcy7XRfqQIvUYtlUilA";
    private static final String CLIENT_SECRET = "EPzXPhLAJoe95kzhnVdADRdpsCiKKJ_jKpknEmTHi5iexc84ohlmfRdH7d3IxTzJGiLuFFFYTANpfEUl";
    private static String accessToken;

    private static String credentials() {
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    private static void getAccessToken() throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api-m.sandbox.paypal.com/v1/oauth2/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + credentials())
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        HttpResponse<String> postResponse;
        HttpClient client = HttpClient.newHttpClient();
        postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        String responseBody = postResponse.body();
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        accessToken = jsonObject.get("access_token").getAsString(); // Removed 'this'
    }

    private static String getJsonBody(int paymentAmount) {
        JsonObject amount = new JsonObject();
        amount.addProperty("currency_code", "USD");
        amount.addProperty("value", String.valueOf(paymentAmount));

        JsonObject purchaseUnit = new JsonObject();
        purchaseUnit.add("amount", amount);

        JsonArray purchaseUnits = new JsonArray();
        purchaseUnits.add(purchaseUnit);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("intent", "CAPTURE");
        requestBody.add("purchase_units", purchaseUnits);

        return requestBody.toString();
    }

    public static String createOrder(int paymentAmount) throws IOException, InterruptedException {
        getAccessToken();
        String jsonBody = getJsonBody(paymentAmount);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api-m.sandbox.paypal.com/v2/checkout/orders"))
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .header("Authorization", "Bearer " + accessToken) // Fixed header
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> postResponse;
        HttpClient client = HttpClient.newHttpClient();
        postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        String responseBody = postResponse.body();
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        System.out.println(jsonObject);
        System.out.println("Order ID: " + jsonObject.get("id").getAsString());
        return jsonObject.get("id").getAsString();
    }

    public static String waitForOrderApproval(String orderId) throws InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();
        int maxRetries = 10;
        int retryInterval = 3000;
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        for (int i = 0; i < maxRetries; i++) {
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            JsonObject responseJson = JsonParser.parseString(getResponse.body()).getAsJsonObject();
            String status = responseJson.get("status").getAsString();

            System.out.println("Order status: " + status);

            if ("APPROVED".equalsIgnoreCase(status)) {
                return captureOrder(orderId);
            }

            Thread.sleep(retryInterval);
        }

        throw new RuntimeException("Payment approval timed out.");
    }


    public static String captureOrder(String orderId) {
        String jsonPayload = "{}";

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/capture"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            String responseBody = postResponse.body();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

            if(!jsonObject.has("status")) {
                System.out.println("approved");
                return jsonObject.get("status").getAsString();
            }else{
                System.out.println("Not yet approved");
                return "Not yet approved";
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Order capture request was interrupted: " + e);
        }
    }
}
