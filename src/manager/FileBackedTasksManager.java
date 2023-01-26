package manager;

import exceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;


public class FileBackedTasksManager extends InMemoryTaskManager {

    private final Path FILE_BACKED_MEMORY;

    public FileBackedTasksManager() {
        FILE_BACKED_MEMORY = null;
    }

    public FileBackedTasksManager(String path) {
        this.FILE_BACKED_MEMORY = Paths.get(path);
    }

    private FileBackedTasksManager(String path, HashMap<Integer, Task> tasks, List<Integer> historyIds) {
        this(path);

        for (Task task : tasks.values()) {
            switch (task.getTaskType()) {
                case TASK:
                    super.createTask(task);
                    break;
                case EPIC:
                    super.createEpic((Epic) task);
                    break;
                case SUBTASK:
                    super.createSubtask((Subtask) task);
                    break;
            }
        }

        for (int id : historyIds) {
            Task task = tasks.get(id);
            this.historyManager.add(task);
        }
    }

    public static void main(String[] args) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir"), "data.csv");

        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        FileBackedTasksManager tasksManager = new FileBackedTasksManager(path.toString());

        InMemoryTaskManager.addTasks(tasksManager);

        tasksManager.getEpicById(3);
        tasksManager.getTaskById(0);
        tasksManager.getSubtaskById(4);
        tasksManager.getSubtaskById(5);
        tasksManager.getTaskById(1);
        tasksManager.getEpicById(2);
        tasksManager.getSubtaskById(6);

        FileBackedTasksManager recoveredTaskManager = FileBackedTasksManager.loadFromFile(path);

        List<Task> history = tasksManager.getHistory();
        List<Task> recoveredHistory = recoveredTaskManager.getHistory();

        List<Task> tasks = tasksManager.getAllTasks();
        List<Task> recoveredTasks = recoveredTaskManager.getAllTasks();

        List<Epic> epics = tasksManager.getAllEpics();
        List<Epic> recoveredEpics = recoveredTaskManager.getAllEpics();

        List<Subtask> subtasks = tasksManager.getAllSubtasks();
        List<Subtask> recoveredSubtasks = recoveredTaskManager.getAllSubtasks();

        System.out.println(history.equals(recoveredHistory));
        System.out.println(tasks.equals(recoveredTasks));
        System.out.println(epics.equals(recoveredEpics));
        System.out.println(subtasks.equals(recoveredSubtasks));
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager taskManager = null;

        try (BufferedReader br = new BufferedReader(new FileReader(path.toString(), StandardCharsets.UTF_8))) {
            br.readLine();
            HashMap<Integer, Task> tasks = getParsedMapOfTasks(br);
            List<Integer> historyIds = getParsedHistoryIds(br);
            taskManager = new FileBackedTasksManager(path.toString(), tasks, historyIds);

        } catch (IOException e) {
            System.out.println("Процесс загрузки из файла завершился с ошибкой.");
        }
        return taskManager;
    }


    private static HashMap<Integer, Task> getParsedMapOfTasks(BufferedReader br) throws IOException {
        HashMap<Integer, Task> tasks = new HashMap<>();

        while (br.ready()) {
            String line = br.readLine();

            if (line.isEmpty()) {
                break;
            }

            TaskType type = TaskType.valueOf(line.split(",")[1]);

            switch (type) {
                case TASK:
                    Task task = Task.deserialize(line);
                    tasks.put(task.getId(), task);
                    break;
                case EPIC:
                    Epic epic = Epic.deserialize(line);
                    tasks.put(epic.getId(), epic);
                    break;
                case SUBTASK:
                    Subtask subtask = Subtask.deserialize(line);
                    int epicId = Integer.parseInt(line.split(",")[line.split(",").length - 1]);
                    subtask.setEpic((Epic) tasks.get(epicId));
                    tasks.put(subtask.getId(), subtask);
                    break;
            }
        }
        return tasks;
    }

    private static List<Integer> getParsedHistoryIds(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null || line.isEmpty()) {
            return new ArrayList<>();
        }
        return HistoryManager.deserialize(line);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.FILE_BACKED_MEMORY.toString(), StandardCharsets.UTF_8))) {
            setHeader(bw);
            saveTasksToFile(bw);
            bw.write(System.lineSeparator());
            saveHistoryToFile(bw);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения.");
        }
    }

    private void setHeader(BufferedWriter bw) throws IOException {
        String header = "id,type,name,status,description,startDate, duration,epic";
        bw.write(header + System.lineSeparator());
    }

    private void saveTasksToFile(BufferedWriter bw) throws IOException {
        List<Task> tasks = new ArrayList<>();
        Stream.of(getAllTasks(), getAllEpics(), getAllSubtasks()).forEach(tasks::addAll);

        for (Task task : tasks) {
            String newLine = task.serialize() + System.lineSeparator();
            bw.write(newLine);
        }
    }

    private void saveHistoryToFile(BufferedWriter bw) throws IOException {
        bw.write(HistoryManager.serialize(this.historyManager));
    }
}
