package manager;

import java.io.IOException;
import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefault() throws IOException {
        return new HTTPTaskManager("http://localhost:8078");
    }

    public static TaskManager getFileBackedManager(Path path) {
        return new FileBackedTasksManager(path.toString());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
