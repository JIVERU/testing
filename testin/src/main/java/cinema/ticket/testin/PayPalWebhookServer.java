//import static spark.Spark.*;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//
//public class PayPalWebhookServer {
//    public static void main(String[] args) {
//        port(8080); // Server listens on port 8080
//
//        // Define the webhook endpoint
//        post("/webhook", (request, response) -> {
//            String body = request.body();
//            JsonObject webhookEvent = JsonParser.parseString(body).getAsJsonObject();
//
//            // Extract relevant information
//            String eventType = webhookEvent.get("event_type").getAsString();
//            String orderId = webhookEvent.getAsJsonObject("resource").get("id").getAsString();
//
//            // Log the event
//            System.out.println("Webhook received. Event: " + eventType + ", Order ID: " + orderId);
//
//            // Respond to PayPal
//            response.status(200);
//            return "Webhook received";
//        });
//    }
//}
