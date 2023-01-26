package manager;

import kVServer.KVTaskClient;
import task.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private static Gson gson;

    static {
        gson = new Gson();
    }

    public HTTPTaskManager(String serverURL) throws IOException {
        this.kvTaskClient = new KVTaskClient(serverURL);
    }

    public HTTPTaskManager(List<Task> tasks, List<Epic> epics, List<Integer> historyIds, String serverURL) throws IOException {
        this.kvTaskClient = new KVTaskClient(serverURL);

        // Add tasks
        for (Task task : tasks) {
            this.createTask(task);
        }

        // Add epics
        for (Epic epic : epics) {
            this.createEpic(epic);
        }

        for (int i = historyIds.size() - 1; i >= 0; i--) {
            int id = historyIds.get(i);
            if (this.tasks.containsKey(id)) {
                this.historyManager.add(this.tasks.get(id));
            } else if (this.epics.containsKey(id)) {
                this.historyManager.add(this.epics.get(id));
            } else if (this.findSubtask(id) != null) {
                this.historyManager.add(findSubtask(id));
            }
        }
    }

    public static HTTPTaskManager recover(String serverURL) throws IOException {
        var kvTaskClient = new KVTaskClient(serverURL);

        // Tasks
        Type tasksType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> tasks = gson.fromJson(kvTaskClient.load("tasks"), tasksType);

        // Epics
        Type epicsType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        ArrayList<Epic> epics = gson.fromJson(kvTaskClient.load("epics"), epicsType);

        // History
        Type historyType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        ArrayList<Integer> historyIds = gson.fromJson(kvTaskClient.load("history"), historyType);


        return new HTTPTaskManager(tasks, epics, historyIds, serverURL);
    }

    @Override
    public void save() {
        kvTaskClient.put("tasks", gson.toJson(this.getAllTasks()));
        kvTaskClient.put("epics", gson.toJson(this.getAllEpics()));
        kvTaskClient.put("history", gson.toJson(this.getHistory().stream().map(x -> x.getId()).collect(Collectors.toList())));
    }
}
