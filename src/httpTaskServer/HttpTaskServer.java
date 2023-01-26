package httpTaskServer;

import manager.Managers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {

    private final HttpServer server;
    private static final int PORT = 8080;

    public HttpTaskServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", new TasksHandler());
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        this.server.stop(0);
    }

    public static class TasksHandler implements HttpHandler {

        private final Mapper mapper;

        public TasksHandler() throws IOException {
            this.mapper = new Mapper(Managers.getDefault());
        }

        @Override
        public void handle(final HttpExchange httpExchange) throws IOException {
            final var path = httpExchange.getRequestURI().toString();
            final var requestMethod = httpExchange.getRequestMethod();
            Response response;

            switch (requestMethod) {
                case "GET":
                    response = mapper.executeGetRequest(path);
                    sendResponse(response, httpExchange);
                    break;
                case "POST":
                    try (InputStream inputStream = httpExchange.getRequestBody()) {
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        response = mapper.executePostRequest(path, body);
                        sendResponse(response, httpExchange);
                    }
                    break;
                case "DELETE":
                    response = mapper.executeDeleteRequest(path);
                    sendResponse(response, httpExchange);
                    break;
                default:
                    response = Response.badRequest();
                    sendResponse(response, httpExchange);
                    break;
            }

            httpExchange.close();
        }

        private void sendResponse(Response response, HttpExchange httpExchange) throws IOException {
            httpExchange.sendResponseHeaders(response.getCode(), 0);

            if (response.getJsonBody() == null) {
                return;
            }

            try (final var outputStream = httpExchange.getResponseBody()) {
                outputStream.write(response.getJsonBody().getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}