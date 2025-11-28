package tasktracker.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;

public abstract class AbstractHandler {

    protected void sendResponse(Integer code, String responseBody, HttpExchange exchange) {

        try {
            byte[] bytes = responseBody.getBytes();

            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

            exchange.sendResponseHeaders(code, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }

        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    protected void sendNotFound(HttpExchange exchange, String message) {
        sendResponse(404, "{\"error\":\"" + message + "\"}", exchange);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) {
        sendResponse(406, "{\"error\":\"" + message + "\"}", exchange);
    }

    protected void sendText(HttpExchange exchange, String message) {
        sendResponse(200, message, exchange);
    }

}
