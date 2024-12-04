module cinema.ticket.testin {
    requires javafx.fxml;
    requires com.google.gson;
    requires okhttp3;
    requires javafx.web;
    requires java.net.http;


    opens cinema.ticket.testin to javafx.fxml;
    exports cinema.ticket.testin;
}