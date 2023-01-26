package tests;

import kVServer.KVServer;
import manager.HTTPTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class HttpTasksManagerTest extends TaskManagerTest {
    private final String serverURL = "http://localhost:8078";

    public HttpTasksManagerTest() throws IOException {
        this.manager = new HTTPTaskManager(serverURL);
    }

    @Test
    void recoverSuccess() throws IOException {
        // Создаем историю просмотра
        this.manager.getEpicById(3);
        this.manager.getSubtaskById(5);
        this.manager.getTaskById(1);

        var recovered = HTTPTaskManager.recover(serverURL);

        Assertions.assertEquals(manager.getAllTasks(), recovered.getAllTasks());
        Assertions.assertEquals(manager.getAllEpics(), recovered.getAllEpics());
        Assertions.assertEquals(manager.getAllSubtasks(), recovered.getAllSubtasks());
        Assertions.assertEquals(manager.getHistory(), recovered.getHistory());
    }

    @Test
    void recoverEmptyHistorySuccess() throws IOException {
        var recovered = HTTPTaskManager.recover(serverURL);

        Assertions.assertEquals(manager.getAllTasks(), recovered.getAllTasks());
        Assertions.assertEquals(manager.getAllEpics(), recovered.getAllEpics());
        Assertions.assertEquals(manager.getAllSubtasks(), recovered.getAllSubtasks());
        Assertions.assertEquals(manager.getHistory(), recovered.getHistory());
    }

    @BeforeAll
    static void setUp() throws IOException {
        var kvs = new KVServer();
        kvs.start();
    }
}