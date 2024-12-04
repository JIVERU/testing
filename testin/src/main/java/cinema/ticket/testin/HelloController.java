package cinema.ticket.testin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private Button fetchTokenButton;

    @FXML
    private WebView webview;
    private WebEngine engine;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        engine = webview.getEngine();
        fetchTokenButton.setOnAction(event -> {
            try {
                String id = practice.createOrder(400);
                loadPage("https://www.sandbox.paypal.com/checkoutnow?token="+id,id);
            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setText("Failed to fetch access token.");
            }
        });
    }

    public void loadPage(String url, String id) throws IOException, InterruptedException {
        engine.load(url);
        Thread.sleep(5000);
        practice.waitForOrderApproval(id);
    }
}
