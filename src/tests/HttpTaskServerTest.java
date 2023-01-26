package tests;

import httpTaskServer.HttpTaskServer;
import task.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServerTest {

    private final HttpClient client;
    private final String serverURL;
    Gson gson;

    public HttpTaskServerTest() {
        client = HttpClient.newHttpClient();
        serverURL = "http://localhost:8080";
        gson = new Gson();
    }

    List<Task> tasks = List.of(
            new Task(1, "Задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.MAX, 0),
            new Task(2, "Задача 2", "Описание 2", TaskStatus.NEW, LocalDateTime.MAX, 0));

    List<Subtask> subtasks = List.of(
            new Subtask(5, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, null, LocalDateTime.MAX, 0),
            new Subtask(6, "Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, null, LocalDateTime.MAX, 0),
            new Subtask(7, "Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW, null, LocalDateTime.MAX, 0)
    );

    List<Epic> epics = List.of(
            new Epic(3, "Эпик 1", "Описание 1", TaskStatus.NEW),
            new Epic(4, "Эпик 2", "Описание 2", TaskStatus.NEW)
    );

    // --GET--
    @Test
    public void getTask(){
        URI url = URI.create(serverURL + "/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void getEpic(){
        URI url = URI.create(serverURL + "/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void getSubtask(){
        URI url = URI.create(serverURL + "/tasks/subtask/?id=5");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void getAllTasks(){
        URI url = URI.create(serverURL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void getAllEpics(){
        URI url = URI.create(serverURL + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void getAllSubtasks(){
        URI url = URI.create(serverURL + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void getHistory(){
        URI url = URI.create(serverURL + "/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void getPrioritizedTasks(){
        URI url = URI.create(serverURL + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void getEpicSubtasks(){
        URI url = URI.create(serverURL + "/tasks/subtask/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    // --DELETE--
    @Test
    public void deleteAllTasks() {
        URI url = URI.create(serverURL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void deleteAllEpics() {
        URI url = URI.create(serverURL + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void deleteAllSubtasks() {
        URI url = URI.create(serverURL + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void deleteTask() {
        URI url = URI.create(serverURL + "/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void deleteEpic() {
        URI url = URI.create(serverURL + "/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void deleteSubtask() {
        URI url = URI.create(serverURL + "/tasks/subtask/?id=5");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    // POST
    @Test
    public void createTaskSuccess() {
        Task task = new Task(11, "Задача 11", "Описание 11", TaskStatus.NEW, LocalDateTime.MAX, 0);
        URI url = URI.create(serverURL + "/tasks/task/");
        String json = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void createEpicSuccess() {
        Epic epic = new Epic(11, "Эпик 1", "Описание 1", TaskStatus.NEW);
        URI url = URI.create(serverURL + "/tasks/epic/");
        String json = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    @Test
    public void createSubtaskSuccess() {
        Subtask subtask = new Subtask(11, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epics.get(0), LocalDateTime.MAX, 0);
        URI url = URI.create(serverURL + "/tasks/subtask/");
        String json = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        Assertions.assertEquals(200, sendRequest(request).statusCode());
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + request.uri() + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            return null;
        }
    }

    @BeforeAll
    static void setUp() throws IOException {
        var ts = new HttpTaskServer();
        ts.start();
    }

    @BeforeEach
    void renewStorage() {
        clearStorage();

        for (Task task : tasks) {
            URI uri = URI.create(serverURL + "/tasks/task/");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                    .build();
            sendRequest(request);
        }

        for (Epic epic : epics) {
            URI uri = URI.create(serverURL + "/tasks/epic/");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                    .build();
            sendRequest(request);
        }

        for (Subtask subtask : subtasks) {
            subtask.setEpic(epics.get(0));
            URI uri = URI.create(serverURL + "/tasks/subtask/");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                    .build();
            sendRequest(request);
        }
    }

    void clearStorage() {
        var urls = List.of(
                URI.create(serverURL + "/tasks/task/"),
                URI.create(serverURL + "/tasks/epic/"));

        for (var url : urls) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .DELETE()
                    .build();
            sendRequest(request);
        }
    }
}
